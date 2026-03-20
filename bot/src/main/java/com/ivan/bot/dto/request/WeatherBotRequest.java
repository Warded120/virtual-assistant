package com.ivan.bot.dto.request;

public record WeatherBotRequest(String city, Long chatId) implements BotRequest {
}
