package com.search.keyword.controller;

import com.search.keyword.dto.SearchRequestDto;
import com.search.keyword.dto.SearchResponseDto;
import com.search.keyword.dto.TopKeywordResponseDto;
import com.search.keyword.service.BlogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {
    private final BlogService blogService;

    @PostMapping("/search")
    private SearchResponseDto blogSearch(@Valid @RequestBody SearchRequestDto requestDto){
        return blogService.blogSearch(requestDto);
    }

    @GetMapping("/top/keyword")
    private List<TopKeywordResponseDto> topKeywordSearch(){
        return blogService.getKeywordTopList();
    }
}
