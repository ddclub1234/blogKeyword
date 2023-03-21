package com.search.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverSearchResponseDto {
    private String lastBuildDate;
    private Integer total;
    private Integer start;
    private Integer display;
    private List<NaverItemDto> items;

}
