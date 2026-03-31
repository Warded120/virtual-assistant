package com.ivan.bot.dto.response;

import com.ivan.bot.enumeration.Language;
import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Builder
public class EventResponse implements BotResponse {

    private boolean success;
    private String title;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Language language;
    private String errorMessage;
    private File icsFile;
    private boolean flowStarted;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Override
    public String getMessage() {
        return getMessage(language != null ? language : Language.ENGLISH);
    }

    @Override
    public String getMessage(Language lang) {
        if (flowStarted) {
            if (lang == Language.UKRAINIAN) {
                return "📅 Давайте створимо подію!\n\nЯка буде назва події?";
            }
            return "📅 Let's create an event!\n\nWhat will be the event title?";
        }

        if (!success) {
            if (lang == Language.UKRAINIAN) {
                return "❌ Помилка створення події: " + (errorMessage != null ? errorMessage : "невідома помилка");
            }
            return "❌ Failed to create event: " + (errorMessage != null ? errorMessage : "unknown error");
        }

        String formattedStart = startDateTime != null ? startDateTime.format(FORMATTER) : "N/A";
        String formattedEnd = endDateTime != null ? endDateTime.format(FORMATTER) : "N/A";

        if (lang == Language.UKRAINIAN) {
            return String.format(
                    "✅ Подію створено!\n" +
                    "📝 Назва: %s\n" +
                    "🕐 Початок: %s\n" +
                    "🕑 Кінець: %s\n\n" +
                    "📎 Надсилаю вам .ics файл для додавання до календаря.",
                    title,
                    formattedStart,
                    formattedEnd
            );
        }

        return String.format(
                "✅ Event created!\n" +
                "📝 Title: %s\n" +
                "🕐 Start: %s\n" +
                "🕑 End: %s\n\n" +
                "📎 Sending you the .ics file to add to your calendar.",
                title,
                formattedStart,
                formattedEnd
        );
    }
}

