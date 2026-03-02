package com.ivan.api.controller;

import com.ivan.api.dto.CurrencyResponse;
import com.ivan.api.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;

    /**
     * Отримати курси валют
     * 
     * @param base Базова валюта (за замовчуванням USD)
     * @return Курси валют
     * 
     * Приклад: GET /api/v1/currency?base=EUR
     */
    @GetMapping
    public ResponseEntity<CurrencyResponse> getExchangeRates(
            @RequestParam(defaultValue = "USD") String base) {
        
        log.info("Received request for exchange rates with base: {}", base);
        
        if (base == null || base.trim().isEmpty()) {
            throw new IllegalArgumentException("Base currency parameter is required");
        }
        
        CurrencyResponse response = currencyService.getExchangeRates(base.trim());
        return ResponseEntity.ok(response);
    }

    /**
     * Отримати курси валют (альтернативний шлях)
     * 
     * @param base Базова валюта
     * @return Курси валют
     * 
     * Приклад: GET /api/v1/currency/EUR
     */
    @GetMapping("/{base}")
    public ResponseEntity<CurrencyResponse> getExchangeRatesByPath(
            @PathVariable String base) {
        
        log.info("Received path request for exchange rates with base: {}", base);
        
        CurrencyResponse response = currencyService.getExchangeRates(base);
        return ResponseEntity.ok(response);
    }
}