package com.ivan.bot.service;

import com.ivan.bot.client.currency.CurrencyClient;
import com.ivan.bot.client.weather.WeatherClient;
import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.dto.request.CurrencyBotRequest;
import com.ivan.bot.dto.request.WeatherBotRequest;
import com.ivan.bot.dto.response.BotResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NlpDecisionService {
    private final CurrencyClient currencyClient;
    private final WeatherClient weatherClient;


    public BotResponse decideResponse(BotRequest botRequest) {
        return switch (botRequest) {
            case WeatherBotRequest weatherRequest ->
                weatherClient.getWeather(weatherRequest);
            case CurrencyBotRequest currencyRequest ->
                currencyClient.getCurrencyRates(currencyRequest);
            default -> throw new IllegalStateException("Unexpected value: " + botRequest);
        };
    }
}
