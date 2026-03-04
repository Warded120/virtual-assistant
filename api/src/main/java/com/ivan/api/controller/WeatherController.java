package com.ivan.api.controller;

import com.ivan.api.dto.WeatherResponse;
import com.ivan.api.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * example: GET /api/v1/weather?city=Kyiv
     */
    @GetMapping
    public ResponseEntity<WeatherResponse> getWeather(
            @RequestParam(defaultValue = "Kyiv") String city
    ) {
        
        log.info("Received request for weather in city: {}", city);
        
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City parameter is required");
        }
        
        WeatherResponse response = weatherService.getWeather(city.trim());
        return ResponseEntity.ok(response);
    }
}