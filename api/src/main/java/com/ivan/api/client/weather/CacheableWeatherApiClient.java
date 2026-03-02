package com.ivan.api.client.weather;

import com.ivan.api.cache.GenericCache;
import com.ivan.api.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component("weatherApiClient")
public class CacheableWeatherApiClient implements WeatherApiClient {
    public static final String UNITS = "metric";
    private final WeatherApiClient nonCacheableWeatherApiClient;
    private final GenericCache<String, Optional<WeatherResponse.ExternalWeatherResponse>> cache;

    @Value("${api.weather.key}")
    private String weatherApiKey;

    @Override
    public Optional<WeatherResponse.ExternalWeatherResponse> getWeather(String city, String apiKey, String units) {
        return cache.of(cityKey -> nonCacheableWeatherApiClient.getWeather(cityKey, weatherApiKey, UNITS))
                .apply(city);
    }
}