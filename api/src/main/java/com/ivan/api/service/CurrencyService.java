package com.ivan.api.service;

import com.ivan.api.dto.CurrencyResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;

@Slf4j
@Service
public class CurrencyService {

    private final WebClient webClient;
    
    @Value("${api.currency.url}")
    private String currencyApiUrl;
    
    @Value("${api.currency.timeout}")
    private long timeout;

    public CurrencyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "currencyCache", key = "#baseCurrency")
    public CurrencyResponse getExchangeRates(String baseCurrency) {
        log.info("Fetching exchange rates for base currency: {}", baseCurrency);
        
        try {
            CurrencyResponse.ExternalCurrencyResponse externalResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.exchangerate-api.com")
                    .path("/v4/latest/" + baseCurrency.toUpperCase())
                    .build())
                .retrieve()
                .bodyToMono(CurrencyResponse.ExternalCurrencyResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (externalResponse == null) {
                throw new ExternalApiException("No response from currency API");
            }

            log.info("Successfully fetched exchange rates for: {}", baseCurrency);
            
            return CurrencyResponse.builder()
                .base(externalResponse.getBase())
                .date(externalResponse.getDate())
                .rates(externalResponse.getRates())
                .timestamp(System.currentTimeMillis())
                .build();
                
        } catch (WebClientResponseException e) {
            log.error("Currency API error for base {}: status={}, body={}", 
                baseCurrency, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException(
                "Failed to fetch currency data: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching currency rates for {}: {}", 
                baseCurrency, e.getMessage(), e);
            throw new ExternalApiException(
                "Currency service unavailable: " + e.getMessage(), e);
        }
    }
}