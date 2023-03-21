package com.search.keyword.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequestDto {
    @NotBlank(message = "검색 키워드는 필수입니다.")
    private String query;
    private String sort;
    @Min(1)
    @Max(50)
    private Integer page;
    @Min(1)
    @Max(50)
    private Integer size;
}
