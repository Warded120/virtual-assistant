package com.ivan.bot.response.builder.impl;

import com.ivan.bot.dto.WeatherResponse;
import com.ivan.bot.response.builder.SendMessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class WeatherSendMessageBuilder implements SendMessageBuilder<WeatherResponse> {
    @Override
    public String buildResponse(WeatherResponse data) {
        return String.format(
                "Weather in %s, %s: %s. Temperature: %.1f°C, feels like %.1f°C. Humidity: %d%%. Wind speed: %.1f m/s.",
                data.getCity(),
                data.getCountry(),
                data.getDescription(),
                data.getTemperature(),
                data.getFeelsLike(),
                data.getHumidity(),
                data.getWindSpeed()
        );    }
}
