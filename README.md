# 블로그 검색 서비스

## 1. 개발환경
  - java 17
  - spring boot '2.7.9'
  - gradle
  - h2(in-memory)
  
## 2. API 명세(port:8089)
### 1) 블로그 검색
     - api : /blog/search
     - Request body 
     {
        "query" : 검색키워드(*필수*)
        , "sort" : 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
        , "page" : 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
        , "size" : 한 페이지에 보여질 문서 수, 1~50 사이의 값, 기본 값 10
     }
     - Response Body
     {
        "meta": {
           "total_count": 검색된 문서 수,
           "pageable_count": total_count 중 노출 가능 문서 수,
           "is_end": 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
        },
        "documents": [
           {
            "title": 블로그 글 제목,
            "contents": 블로그 글 요약,
            "url": 블로그 글 URL,
            "blogname": 블로그의 이름,
            "thumbnail": 검색 시스템에서 추출한 대표 미리보기 이미지 URL, 미리보기 크기 및 화질은 변경될 수 있음,
            "datetime": 블로그 글 작성시간
          }
      ]
   }
