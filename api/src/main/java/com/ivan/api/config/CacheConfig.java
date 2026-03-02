package com.ivan.api.config;

import com.ivan.api.cache.GenericCache;
import com.ivan.api.dto.CurrencyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;
import java.util.Optional;

@Configuration
public class CacheConfig {

    @Value("${cache.item.expiration}")
    private Duration ttl;

    @Bean
    public GenericCache<String, Optional<CurrencyResponse.ExternalCurrencyResponse>> currencyResponseCache() {
        return new GenericCache<>(ttl);
    }
}
