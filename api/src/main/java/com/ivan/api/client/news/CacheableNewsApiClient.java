package com.ivan.api.client.news;

import com.ivan.api.cache.GenericCache;
import com.ivan.api.dto.NewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component("newsApiClient")
public class CacheableNewsApiClient implements NewsApiClient {
    private final NewsApiClient nonCacheableNewsApiClient;
    private final GenericCache<String, Optional<NewsResponse.ExternalNewsResponse>> cache;

    @Value("${api.news.key}")
    private String newsApiKey;

    @Override
    public Optional<NewsResponse.ExternalNewsResponse> getNews(String country, String apiKey) {
        return cache.of(countryKey -> nonCacheableNewsApiClient.getNews(countryKey, newsApiKey))
                .apply(country);
    }
}