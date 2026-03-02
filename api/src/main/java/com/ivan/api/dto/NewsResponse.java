package com.ivan.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsResponse {
    
    private Integer totalResults;
    private List<Article> articles;
    private Long timestamp;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Article {
        private String title;
        private String description;
        private String url;
        private String source;
        private String publishedAt;
        private String author;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalNewsResponse {
        private String status;
        private Integer totalResults;
        private List<ExternalArticle> articles;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ExternalArticle {
            private Source source;
            private String author;
            private String title;
            private String description;
            private String url;
            private String publishedAt;
            
            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Source {
                private String name;
            }
        }
    }
}
