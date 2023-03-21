package com.search.keyword;

import com.search.keyword.dto.NaverSearchResponseDto;
import com.search.keyword.dto.SearchRequestDto;
import com.search.keyword.dto.SearchResponseDto;
import com.search.keyword.dto.TopKeywordResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class KeywordApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void test() {

        //search
        HttpHeaders headers = new HttpHeaders();
        SearchRequestDto requestDto = SearchRequestDto.builder()
                .query("테스트")
                .sort("accuracy")
                .page(2)
                .size(10)
                .build();
        HttpEntity<SearchRequestDto> entity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<SearchResponseDto> response = testRestTemplate.exchange("/blog/search",HttpMethod.POST,entity, SearchResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        //get Top keyword List
        ResponseEntity<List<TopKeywordResponseDto>> topKeyResponse = testRestTemplate.exchange("/blog/top/keyword", HttpMethod.GET, null, new ParameterizedTypeReference<List<TopKeywordResponseDto>>() {});
        assertThat(topKeyResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
