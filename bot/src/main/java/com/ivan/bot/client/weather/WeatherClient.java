package com.ivan.bot.client.weather;

import com.ivan.bot.dto.request.WeatherBotRequest;
import com.ivan.bot.dto.response.BotResponse;
import com.ivan.bot.dto.response.WeatherResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Optional;

@RequiredArgsConstructor
@Component("weatherApiClient")
public class WeatherClient {
    public static final String UNITS = "metric";
    private final WeatherApiClient nonCacheableWeatherApiClient;

    @Value("${api.weather.key}")
    private String weatherApiKey;

    public BotResponse getWeather(WeatherBotRequest weatherRequest) {
        var externalResponse =
                nonCacheableWeatherApiClient
                        .getWeather(weatherRequest.city(), weatherApiKey, UNITS)
                        .orElseThrow(() -> new RuntimeException("Something went wrong during weather API request"));

        return WeatherResponse.builder()
                .city(externalResponse.getName())
                .country(externalResponse.getSys().getCountry())
                .temperature(externalResponse.getMain().getTemp())
                .feelsLike(externalResponse.getMain().getFeelsLike())
                .humidity(externalResponse.getMain().getHumidity())
                .description(externalResponse.getWeather()[0].getDescription())
                .windSpeed(externalResponse.getWind().getSpeed())
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @FeignClient(
            name = "nonCacheableWeatherApiClient",
            url = "${api.weather.base-url}",
            primary = false,
            qualifiers = "nonCacheableWeatherApiClient",
            dismiss404 = true //return Optional.empty() and don't throw FeignException$NotFound if status is 404
    )
    private interface WeatherApiClient {
        @GetMapping("/data/2.5/weather")
        Optional<WeatherResponse.ExternalWeatherResponse> getWeather(
                @RequestParam("q") String city,
                @RequestParam("appid") String apiKey,
                @RequestParam(value = "units", defaultValue = "metric") String units
        );
    }
}