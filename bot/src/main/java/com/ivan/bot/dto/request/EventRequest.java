package com.ivan.bot.dto.request;

import com.ivan.bot.enumeration.Language;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventRequest implements BotRequest {
    private Long chatId;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Language detectedLanguage;
}

