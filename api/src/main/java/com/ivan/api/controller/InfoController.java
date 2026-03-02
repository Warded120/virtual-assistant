package com.ivan.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class InfoController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Information API");
        info.put("version", "1.0.0");
        info.put("description", "REST API для отримання корисної інформації");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("weather", "/api/v1/weather?city={city}");
        endpoints.put("currency", "/api/v1/currency?base={currency}");
        endpoints.put("news", "/api/v1/news?country={country}&category={category}");
        
        info.put("endpoints", endpoints);
        
        return ResponseEntity.ok(info);
    }
}