package com.search.keyword.service;

import com.search.keyword.dto.*;
import com.search.keyword.entity.Keyword;
import com.search.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlogService {

    private final KeywordRepository keywordRepository;
    private static final String KAKAO_URL = "https://dapi.kakao.com/v2/search/blog";
    private static final String KAKAO_REST_API_KEY = "cb413dbbc66115898c9fa3f53254bcd8";
    private static final String NAVER_URL = "https://openapi.naver.com/v1/search/blog.json";
    private static final String NAVER_CLIENT_ID = "afDeP4ZA28U0ytFydPZa";
    private static final String NAVER_CLIENT_SECRET = "ZN4ApafcYq";

    @Transactional
    public SearchResponseDto blogSearch(SearchRequestDto requestDto){
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<SearchResponseDto> response = kakaoBlogSearch(restTemplate, requestDto);
        SearchResponseDto responseDto = null;
        if(HttpStatus.OK.equals(response.getStatusCode())){
            responseDto = response.getBody();
        }else{
            ResponseEntity<NaverSearchResponseDto> naverResponse = naverBlogSearch(restTemplate, requestDto);
            NaverSearchResponseDto naverSearchResponseDto = naverResponse.getBody();
            List<SearchDocumentDto> searchDocumentDtoList
                    = naverSearchResponseDto.getItems().stream().map(naverItemDto
                       -> SearchDocumentDto.builder()
                                         .url(naverItemDto.getBloggerlink())
                                         .title(naverItemDto.getTitle())
                                         .contents(naverItemDto.getDescription())
                                         .datetime(naverItemDto.getPostdate())
                                         .blogname(naverItemDto.getBloggername()).build()).toList();
            Integer pageCount = naverSearchResponseDto.getTotal()/naverSearchResponseDto.getDisplay();
            if(naverSearchResponseDto.getTotal() % naverSearchResponseDto.getDisplay() > 0){
                pageCount++;
            }

            Boolean isEnd = naverSearchResponseDto.getStart() > (naverSearchResponseDto.getTotal() - naverSearchResponseDto.getDisplay());

            SearchMetaDto searchMetaDto = SearchMetaDto.builder()
                    .total_count(naverSearchResponseDto.getTotal())
                    .is_end(isEnd)
                    .pageable_count(pageCount)
                    .build();
            responseDto = SearchResponseDto.builder().meta(searchMetaDto).documents(searchDocumentDtoList).build();

        }

        keywordRepository.save(Keyword.builder()
                .keyword(requestDto.getQuery())
                .build());

        return responseDto;
    }

    public List<TopKeywordResponseDto> getKeywordTopList(){
        return keywordRepository.getKeywordTopList().stream().map(vo ->
                TopKeywordResponseDto.builder()
                        .keyword(vo.getKeyword())
                        .count(vo.getCount())
                .build()).toList();
    }

    private ResponseEntity<SearchResponseDto> kakaoBlogSearch(RestTemplate restTemplate, SearchRequestDto requestDto){
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(KAKAO_URL)
                    .queryParam("query", URLEncoder.encode(requestDto.getQuery(), StandardCharsets.UTF_8));
            Optional.ofNullable(requestDto.getSort()).ifPresent(s -> {
                if(!"accuracy".equals(s) && !"recency".equals(s)){
                    throw new RuntimeException("sort의 값은 오직 'accuracy'와 'recency'만 가능합니다.");
                }else{
                    uriBuilder.queryParam("sort", s);
                }
            });
            Optional.ofNullable(requestDto.getPage()).ifPresent(i-> uriBuilder.queryParam("page", i));
            Optional.ofNullable(requestDto.getSize()).ifPresent(i-> uriBuilder.queryParam("size", i));
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "KakaoAK "+KAKAO_REST_API_KEY);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(
                    uriBuilder.toUriString(), HttpMethod.GET, entity, SearchResponseDto.class);
        } catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SearchResponseDto());
        }

    }

    private ResponseEntity<NaverSearchResponseDto> naverBlogSearch(RestTemplate restTemplate, SearchRequestDto requestDto){
            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(NAVER_URL)
                    .queryParam("query", URLEncoder.encode(requestDto.getQuery(), StandardCharsets.UTF_8));
            Optional.ofNullable(requestDto.getSort()).ifPresent(s -> {
                if(!"accuracy".equals(s) && !"recency".equals(s)){
                    throw new RuntimeException("sort의 값은 오직 'accuracy'와 'recency'만 가능합니다.");
                }else{
                    uriBuilder.queryParam("sort", "accuracy".equals(s) ? "sim" : "date");
                }
            });
            if(!Objects.isNull(requestDto.getPage()) && !Objects.isNull(requestDto.getSize())){
                uriBuilder.queryParam("start", requestDto.getPage() * requestDto.getSize()+1);
            }
            Optional.ofNullable(requestDto.getSize()).ifPresent(i-> uriBuilder.queryParam("display", i));

            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", NAVER_CLIENT_ID);
            headers.add("X-Naver-Client-Secret", NAVER_CLIENT_SECRET);
            HttpEntity<?> entity = new HttpEntity<>(headers);
            return restTemplate.exchange(
                    uriBuilder.toUriString(), HttpMethod.GET, entity, NaverSearchResponseDto.class);
    }
}
