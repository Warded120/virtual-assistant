package com.ivan.bot.response.builder;

public interface SendMessageBuilder<T> {
    String buildResponse(T data);
}
