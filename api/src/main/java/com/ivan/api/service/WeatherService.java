package com.ivan.api.service;

import com.ivan.api.dto.WeatherResponse;
import com.ivan.api.exception.ExternalApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.time.Duration;

@Slf4j
@Service
public class WeatherService {

    private final WebClient webClient;
    
    @Value("${api.weather.url}")
    private String weatherApiUrl;
    
    @Value("${api.weather.key}")
    private String weatherApiKey;
    
    @Value("${api.weather.timeout}")
    private long timeout;

    public WeatherService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Cacheable(value = "weatherCache", key = "#city")
    public WeatherResponse getWeather(String city) {
        log.info("Fetching weather for city: {}", city);
        
        try {
            WeatherResponse.ExternalWeatherResponse externalResponse = webClient.get()
                .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.openweathermap.org")
                    .path("/data/2.5/weather")
                    .queryParam("q", city)
                    .queryParam("appid", weatherApiKey)
                    .queryParam("units", "metric")
                    .build())
                .retrieve()
                .bodyToMono(WeatherResponse.ExternalWeatherResponse.class)
                .timeout(Duration.ofMillis(timeout))
                .block();

            if (externalResponse == null) {
                throw new ExternalApiException("No response from weather API");
            }

            log.info("Successfully fetched weather for city: {}", city);
            
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
                
        } catch (WebClientResponseException e) {
            log.error("Weather API error for city {}: status={}, body={}", 
                city, e.getStatusCode(), e.getResponseBodyAsString());
            throw new ExternalApiException(
                "Failed to fetch weather data: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching weather for city {}: {}", 
                city, e.getMessage(), e);
            throw new ExternalApiException(
                "Weather service unavailable: " + e.getMessage(), e);
        }
    }
}
