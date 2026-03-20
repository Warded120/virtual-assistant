package com.ivan.bot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ivan.bot.enumeration.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse implements BotResponse {
    
    private String city;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private String description;
    private Double windSpeed;
    private Long timestamp;
    private Language language;

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        if (lang == Language.UKRAINIAN) {
            return String.format(
                    "🌤 Погода в %s, %s: %s.\n" +
                    "🌡 Температура: %.1f°C, відчувається як %.1f°C.\n" +
                    "💧 Вологість: %d%%.\n" +
                    "💨 Швидкість вітру: %.1f м/с.",
                    city, country, description,
                    temperature, feelsLike,
                    humidity, windSpeed
            );
        }
        return String.format(
                "🌤 Weather in %s, %s: %s.\n" +
                "🌡 Temperature: %.1f°C, feels like %.1f°C.\n" +
                "💧 Humidity: %d%%.\n" +
                "💨 Wind speed: %.1f m/s.",
                city, country, description,
                temperature, feelsLike,
                humidity, windSpeed
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExternalWeatherResponse {
        private String name;
        private Main main;
        private Weather[] weather;
        private Wind wind;
        private Sys sys;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Main {
            private Double temp;
            @JsonProperty("feels_like")
            private Double feelsLike;
            private Integer humidity;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Weather {
            private String description;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Wind {
            private Double speed;
        }
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Sys {
            private String country;
        }
    }
}