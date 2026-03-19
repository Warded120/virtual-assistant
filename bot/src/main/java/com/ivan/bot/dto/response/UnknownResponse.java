package com.ivan.bot.dto.response;

public class UnknownResponse implements BotResponse {
    @Override
    public String getMessage() {
        return "I cannot understand your request. Please try something else";
    }
}
