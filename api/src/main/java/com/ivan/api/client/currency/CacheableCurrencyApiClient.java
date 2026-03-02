package com.ivan.api.client.currency;

import com.ivan.api.cache.GenericCache;
import com.ivan.api.dto.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component("currencyApiClient")
public class CacheableCurrencyApiClient implements CurrencyApiClient {
    private final CurrencyApiClient nonCacheableCurrencyApiClient;
    private final GenericCache<String, Optional<CurrencyResponse.ExternalCurrencyResponse>> cache;

    @Override
    public Optional<CurrencyResponse.ExternalCurrencyResponse> getCurrencyRates(String base) {
        return cache.of(nonCacheableCurrencyApiClient::getCurrencyRates)
                .apply(base);
    }
}