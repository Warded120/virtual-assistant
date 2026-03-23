package com.ivan.bot.dto.request;

import com.ivan.bot.enumeration.Language;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReminderRequest implements BotRequest {
    private Long chatId;
    private String reminderText;
    private LocalDateTime reminderDateTime;
    private Language detectedLanguage;
}

