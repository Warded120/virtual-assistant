package com.ivan.api.service;

import com.ivan.api.client.news.NewsApiClient;
import com.ivan.api.dto.NewsResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsApiClient newsApiClient;

    public NewsResponse getTopHeadlines(String country) {
        log.info("Fetching top headlines for country: {}", country);

        var externalResponse = newsApiClient.getNews(country, null)
                .orElseThrow(() -> new ExternalApiException("No news data received from external API"));

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
    }
}