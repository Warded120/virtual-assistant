package com.ivan.api.service;

import com.ivan.api.client.currency.CurrencyApiClient;
import com.ivan.api.dto.CurrencyResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl {

    private final CurrencyApiClient currencyApiClient;

    public CurrencyResponse getExchangeRates(String baseCurrency, String target, float amount) {
        log.info("Fetching exchange rates for base currency: {}", baseCurrency);

        var externalResponse = currencyApiClient.getCurrencyRates(baseCurrency)
                .orElseThrow(() -> new ExternalApiException("Something went wrong during currency API request"));

        var result = externalResponse.getRates().get(target) * amount;
        return CurrencyResponse.builder()
            .result(result)
            .build();
    }
}