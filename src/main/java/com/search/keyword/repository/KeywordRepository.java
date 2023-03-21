package com.search.keyword.repository;

import com.search.keyword.dto.TopKeywordResponseDto;
import com.search.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query(nativeQuery = true, value = "select keyword, count from (\n" +
            "select keyword, count(*) as count  from keyword group by keyword ) \n" +
            "order by count desc\n" +
            "limit 10")
    List<TopKeywordVo> getKeywordTopList();

    interface TopKeywordVo{
        String getKeyword();
        Integer getCount();
    }
}
