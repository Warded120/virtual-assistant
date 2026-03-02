package com.ivan.api.controller;

import com.ivan.api.dto.CurrencyResponse;
import com.ivan.api.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyServiceImpl currencyService;

    /**
     * example: GET /api/v1/currency?base=EUR&target=UAH&amount=1
     */
    @GetMapping
    public ResponseEntity<CurrencyResponse> getExchangeRates(
            @RequestParam(defaultValue = "USD") String base,
            @RequestParam(defaultValue = "UAH") String target,
            @RequestParam(defaultValue = "1") float amount) {
        
        log.info("Received request for exchange rates with base: {}", base);
        
        if (base == null || base.trim().isEmpty()) {
            throw new IllegalArgumentException("Base currency parameter is required");
        }

        if (target == null || target.trim().isEmpty()) {
            throw new IllegalArgumentException("Target currency parameter is required");
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        CurrencyResponse response = currencyService.getExchangeRates(base, target, amount);
        return ResponseEntity.ok(response);
    }
}