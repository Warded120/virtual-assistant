package com.ivan.bot.client.currency;

import com.ivan.bot.cache.GenericCache;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import com.ivan.bot.dto.response.CurrencyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@RequiredArgsConstructor
@Component("currencyApiClient")
public class CurrencyClient {
    private final CurrencyClient.CurrencyApiClient nonCacheableWeatherApiClient;
    private final GenericCache<String, Optional<CurrencyResponse.ExternalCurrencyResponse>> cache;


    public CurrencyResponse getCurrencyRates(CurrencyBotRequest currencyRequest) {
        var externalResponse =
                cache.of(nonCacheableWeatherApiClient::getCurrencyRates)
                        .apply(currencyRequest.base())
                        .orElseThrow(() -> new RuntimeException("Something went wrong during weather API request"));

        return CurrencyResponse.builder()
                .base(externalResponse.getBase())
                .target(currencyRequest.target())
                .rate(externalResponse.getRates().get(currencyRequest.target()))
                .build();
    }

    @FeignClient(
            name = "nonCacheableCurrencyApiClient",
            url = "${api.currency.base-url}",
            primary = false,
            qualifiers = "nonCacheableCurrencyApiClient",
            dismiss404 = true //return Optional.empty() and don't throw FeignException$NotFound if status is 404
    )
    private interface CurrencyApiClient {
        @GetMapping("/v4/latest/{base}")
        Optional<CurrencyResponse.ExternalCurrencyResponse> getCurrencyRates(@PathVariable String base);
    }
}