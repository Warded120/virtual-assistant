package com.ivan.bot.dto.request;

public record CurrencyBotRequest(String base, String target, Long chatId) implements BotRequest {
}
