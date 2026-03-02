package com.ivan.api.controller;

import com.ivan.api.dto.WeatherResponse;
import com.ivan.api.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Test
    void getWeather_Success() throws Exception {
        WeatherResponse mockResponse = WeatherResponse.builder()
            .city("Kyiv")
            .country("UA")
            .temperature(15.5)
            .feelsLike(14.0)
            .humidity(65)
            .description("Clear sky")
            .windSpeed(3.5)
            .timestamp(System.currentTimeMillis())
            .build();

        when(weatherService.getWeather(anyString())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/weather")
                .param("city", "Kyiv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Kyiv"))
                .andExpect(jsonPath("$.temperature").value(15.5));
    }

    @Test
    void getWeather_MissingCity() throws Exception {
        mockMvc.perform(get("/api/v1/weather"))
                .andExpect(status().isBadRequest());
    }
}