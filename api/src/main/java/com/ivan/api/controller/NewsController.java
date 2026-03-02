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
     * Отримати топові новини
     * 
     * @param country Код країни (за замовчуванням us)
     * @param category Категорія новин (за замовчуванням general)
     * @return Список новин
     * 
     * Приклад: GET /api/v1/news?country=ua&category=technology
     * 
     * Доступні категорії: business, entertainment, general, health, 
     *                     science, sports, technology
     */
    @GetMapping
    public ResponseEntity<NewsResponse> getTopHeadlines(
            @RequestParam(defaultValue = "us") String country,
            @RequestParam(defaultValue = "general") String category) {
        
        log.info("Received request for news: country={}, category={}", 
            country, category);
        
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country parameter is required");
        }
        
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category parameter is required");
        }
        
        NewsResponse response = newsService.getTopHeadlines(
            country.trim(), category.trim());
        return ResponseEntity.ok(response);
    }

    /**
     * Отримати топові новини для країни
     * 
     * @param country Код країни
     * @return Список новин
     * 
     * Приклад: GET /api/v1/news/ua
     */
    @GetMapping("/{country}")
    public ResponseEntity<NewsResponse> getTopHeadlinesByCountry(
            @PathVariable String country) {
        
        log.info("Received path request for news in country: {}", country);
        
        NewsResponse response = newsService.getTopHeadlines(country, "general");
        return ResponseEntity.ok(response);
    }
}