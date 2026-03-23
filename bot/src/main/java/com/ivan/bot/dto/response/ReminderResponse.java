package com.ivan.bot.dto.response;

import com.ivan.bot.enumeration.Language;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class ReminderResponse implements BotResponse {

    private boolean success;
    private String reminderText;
    private LocalDateTime reminderDateTime;
    private Language language;
    private String errorMessage;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        if (!success) {
            if (lang == Language.UKRAINIAN) {
                return "❌ Помилка створення нагадування: " + (errorMessage != null ? errorMessage : "невідома помилка");
            }
            return "❌ Failed to create reminder: " + (errorMessage != null ? errorMessage : "unknown error");
        }

        String formattedDateTime = reminderDateTime != null ? reminderDateTime.format(FORMATTER) : "N/A";

        if (lang == Language.UKRAINIAN) {
            return String.format(
                    "✅ Нагадування створено!\n" +
                    "📝 Текст: %s\n" +
                    "🕐 Час: %s",
                    reminderText, formattedDateTime
            );
        }
        return String.format(
                "✅ Reminder created!\n" +
                "📝 Text: %s\n" +
                "🕐 Time: %s",
                reminderText, formattedDateTime
        );
    }
}

