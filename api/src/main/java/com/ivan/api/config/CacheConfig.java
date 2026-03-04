package com.ivan.api.config;

import com.ivan.api.cache.GenericCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${cache.item.expiration}")
    private Duration ttl;

    @Bean
    public <K, V> GenericCache<K, V> currencyResponseCache() {
        return new GenericCache<>(ttl);
    }

//    @Bean
//    public GenericCache<String, Optional<WeatherResponse.ExternalWeatherResponse>> weatherResponseCache() {
//        return new GenericCache<>(ttl);
//    }
//
//    @Bean
//    public GenericCache<String, Optional<NewsResponse.ExternalNewsResponse>> newsResponseCache() {
//        return new GenericCache<>(ttl);
//    }
}
