package com.ivan.bot.handler.impl;

import com.ivan.bot.annotation.Command;
import com.ivan.bot.dto.NewsResponse;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.response.builder.SendMessageBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Command("/news")
@RequiredArgsConstructor
public class NewsHandler implements CommandHandler {
    private final RestTemplate restTemplate;
    private final SendMessageBuilder<NewsResponse> builder;

    @Value("${api.news.endpoint}")
    private String newsEndpoint;

    @Override
    public SendMessage handle(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        var response = restTemplate.getForObject(newsEndpoint, NewsResponse.class);

        return SendMessage.builder()
                .chatId(chatId)
                .text(builder.buildResponse(response))
                .build();
    }
}
