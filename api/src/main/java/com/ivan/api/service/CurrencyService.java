package com.ivan.api.service;

import com.ivan.api.client.CurrencyApiClient;
import com.ivan.api.dto.CurrencyResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyApiClient currencyApiClient;

    public CurrencyResponse getExchangeRates(String baseCurrency) {
        log.info("Fetching exchange rates for base currency: {}", baseCurrency);
        
        CurrencyResponse.ExternalCurrencyResponse externalResponse = currencyApiClient.getCurrencyRates(baseCurrency)
                .orElseThrow(() -> new ExternalApiException("Something went wrong during currency API request"));

        return CurrencyResponse.builder()
            .base(externalResponse.getBase())
            .date(externalResponse.getDate())
            .rates(externalResponse.getRates())
            .timestamp(System.currentTimeMillis())
            .build();
    }
}