package com.ivan.api.service;

import com.ivan.api.dto.NewsResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NewsService {

    private final WebClient webClient;
    
    @Value("${api.news.url}")
    private String newsApiUrl;
    
    @Value("${api.news.key}")
    private String newsApiKey;
    
    @Value("${api.news.timeout}")
    private long timeout;

    public NewsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "newsCache", key = "#country + '-' + #category")
    public NewsResponse getTopHeadlines(String country, String category) {
        log.info("Fetching top headlines for country: {}, category: {}", country, category);
        
        try {
            NewsResponse.ExternalNewsResponse externalResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("newsapi.org")
                    .path("/v2/top-headlines")
                    .queryParam("country", country)
                    .queryParam("category", category)
                    .queryParam("apiKey", newsApiKey)
                    .build())
                .retrieve()
                .bodyToMono(NewsResponse.ExternalNewsResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (externalResponse == null || !"ok".equals(externalResponse.getStatus())) {
                throw new ExternalApiException("No valid response from news API");
            }

            log.info("Successfully fetched {} news articles", 
                externalResponse.getTotalResults());
            
            return NewsResponse.builder()
                .totalResults(externalResponse.getTotalResults())
                .articles(externalResponse.getArticles().stream()
                    .map(article -> NewsResponse.Article.builder()
                        .title(article.getTitle())
                        .description(article.getDescription())
                        .url(article.getUrl())
                        .source(article.getSource().getName())
                        .publishedAt(article.getPublishedAt())
                        .author(article.getAuthor())
                        .build())
                    .collect(Collectors.toList()))
                .timestamp(System.currentTimeMillis())
                .build();
                
        } catch (WebClientResponseException e) {
            log.error("News API error: status={}, body={}", 
                e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException(
                "Failed to fetch news data: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching news: {}", e.getMessage(), e);
            throw new ExternalApiException(
                "News service unavailable: " + e.getMessage(), e);
        }
    }
}