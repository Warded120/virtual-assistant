package com.ivan.bot.dto.response;

import com.ivan.bot.enumeration.Language;

public interface BotResponse {
    String getMessage();

    default String getMessage(Language language) {
        return getMessage();
    }
}
