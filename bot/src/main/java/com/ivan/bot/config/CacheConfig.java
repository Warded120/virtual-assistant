package com.ivan.bot.config;

import com.ivan.bot.cache.GenericCache;
import com.ivan.bot.dto.response.CurrencyResponse;
import com.ivan.bot.dto.response.WeatherResponse;
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
    public <K, V> GenericCache<K, V> currencyResponseCache() {
        return new GenericCache<>(ttl);
    }

    @Bean
    public GenericCache<String, Optional<WeatherResponse.ExternalWeatherResponse>> weatherResponseCache() {
        return new GenericCache<>(ttl);
    }

    @Bean
    public GenericCache<String, Optional<CurrencyResponse.ExternalCurrencyResponse>> newsResponseCache() {
        return new GenericCache<>(ttl);
    }
}
