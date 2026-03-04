package com.ivan.bot.response.builder.impl;

import com.ivan.bot.dto.NewsResponse;
import com.ivan.bot.response.builder.SendMessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class NewsSendMessageBuilder implements SendMessageBuilder<NewsResponse> {
    @Override
    public String buildResponse(NewsResponse data) {
        var first = data.getArticles().getFirst();
        return String.format("Title: %s\nDescription: %s\nSource: %s\nPublished At: %s\nAuthor: %s\nURL: %s",
            first.getTitle(),
            first.getDescription(),
            first.getSource(),
            first.getPublishedAt(),
            first.getAuthor(),
            first.getUrl());
    }
}
