package com.ivan.bot.client.currency;

import com.ivan.bot.dto.request.CurrencyBotRequest;
import com.ivan.bot.dto.response.CurrencyResponse;
import com.ivan.bot.dto.response.BotResponse;
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

    public BotResponse getCurrencyRates(CurrencyBotRequest currencyRequest) {
        var externalResponse = nonCacheableWeatherApiClient
                .getCurrencyRates(currencyRequest.base())
                .orElseThrow(() -> new RuntimeException("Something went wrong during weather API request"));

        return CurrencyResponse.builder()
                .base(externalResponse.getBase())
                .target(currencyRequest.target()) //TODO: add target
                .rate(externalResponse.getRates().get(currencyRequest.target())) //TODO: retrieve rate
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
        Optional<com.ivan.bot.dto.response.CurrencyResponse.ExternalCurrencyResponse> getCurrencyRates(@PathVariable String base);
    }
}