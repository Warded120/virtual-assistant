package com.ivan.api.client;

import com.ivan.api.dto.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Optional;

@FeignClient(
        name = "nonCacheableCurrencyApiClient",
        url = "${api.currency.base-url}",
        primary = false,
        qualifiers = "nonCacheableCurrencyApiClient",
        dismiss404 = true //return Optional.empty() and don't throw FeignException$NotFound if status is 404
)
public interface CurrencyApiClient {
    @GetMapping("/v4/latest/{base}")
    Optional<CurrencyResponse.ExternalCurrencyResponse> getCurrencyRates(@PathVariable String base);
}