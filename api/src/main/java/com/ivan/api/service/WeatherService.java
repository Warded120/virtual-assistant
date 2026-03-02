package com.ivan.api.service;

import com.ivan.api.client.weather.WeatherApiClient;
import com.ivan.api.dto.WeatherResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiClient weatherApiClient;

    public WeatherResponse getWeather(String city) {
        log.info("Fetching weather for city: {}", city);

        WeatherResponse.ExternalWeatherResponse externalResponse = weatherApiClient.getWeather(city, null, null)
                .orElseThrow(() -> new ExternalApiException("Something went wrong during weather API request"));

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
}
