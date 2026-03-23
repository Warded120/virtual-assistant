package com.ivan.bot.builder;

import com.ivan.bot.dto.request.BotRequest;

public interface RequestBuilder {
    BotRequest buildRequest(String originalText, String[] tokens, Long chatId);
}
