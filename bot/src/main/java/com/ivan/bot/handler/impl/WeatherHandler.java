package com.ivan.bot.handler.impl;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.dto.WeatherResponse;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.response.builder.SendMessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Command("/weather")
@RequiredArgsConstructor
public class WeatherHandler implements CommandHandler {
    //TODO: use feignClient instead of restTemplate (use ones in api as example)
    private final RestTemplate restTemplate;
    private final SendMessageBuilder<WeatherResponse> builder;

    @Value("${api.weather.endpoint}")
    private String weatherEndpoint;

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        var response = restTemplate.getForObject(weatherEndpoint, WeatherResponse.class);

        return SendMessage.builder()
                .chatId(chatId)
                .text(builder.buildResponse(response))
                .build();
    }
}
