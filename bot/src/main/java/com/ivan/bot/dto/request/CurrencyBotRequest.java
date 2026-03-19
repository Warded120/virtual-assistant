package com.ivan.bot.dto.request;

public record CurrencyBotRequest(String base, String target) implements BotRequest {
}
