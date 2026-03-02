package com.ivan.api.client.weather;

import com.ivan.api.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@FeignClient(
        name = "nonCacheableWeatherApiClient",
        url = "${api.weather.base-url}",
        primary = false,
        qualifiers = "nonCacheableWeatherApiClient",
        dismiss404 = true //return Optional.empty() and don't throw FeignException$NotFound if status is 404
)
public interface WeatherApiClient {
    @GetMapping("/data/2.5/weather")
    Optional<WeatherResponse.ExternalWeatherResponse> getWeather(
            @RequestParam("q") String city,
            @RequestParam("appid") String apiKey,
            @RequestParam(value = "units", defaultValue = "metric") String units
    );
}