package com.ivan.api.client.news;

import com.ivan.api.dto.NewsResponse;
import com.ivan.api.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@FeignClient(
        name = "nonCacheableNewsApiClient",
        url = "${api.news.base-url}",
        primary = false,
        qualifiers = "nonCacheableNewsApiClient",
        dismiss404 = true //return Optional.empty() and don't throw FeignException$NotFound if status is 404
)
public interface NewsApiClient {
    @GetMapping("/v2/top-headlines")
    Optional<NewsResponse.ExternalNewsResponse> getNews(
            @RequestParam(defaultValue = "ua") String country,
            @RequestParam("apiKey") String apiKey
    );
}