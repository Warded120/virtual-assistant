package com.ivan.api.controller;

import com.ivan.api.dto.NewsResponse;
import com.ivan.api.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//TODO: refactor News flow like weather and currency
@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * example: GET /api/v1/news?country=ua
     */
    @GetMapping
    public ResponseEntity<NewsResponse> getTopHeadlines(@RequestParam(defaultValue = "ua") String country) {
        log.info("Received request for news: country={}", country);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country parameter is required");
        }
        
        NewsResponse response = newsService.getTopHeadlines(country.trim());
        return ResponseEntity.ok(response);
    }
}