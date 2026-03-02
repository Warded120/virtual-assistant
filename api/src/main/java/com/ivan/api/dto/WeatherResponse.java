package com.ivan.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponse {
    
    private String city;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private String description;
    private Double windSpeed;
    private Long timestamp;
    
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