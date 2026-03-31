package com.ivan.bot.handler.impl;

import com.ivan.bot.entity.Event;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.enumeration.UserState;
import com.ivan.bot.fsm.UserStateManager;
import com.ivan.bot.handler.CommandHandler;
import com.ivan.bot.service.EventService;
import com.ivan.bot.service.IntentDetectorService;
import com.ivan.bot.service.UserProfileService;
import com.ivan.bot.util.IcsFileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateEventHandler implements CommandHandler {

    private final UserStateManager stateManager;
    private final EventService eventService;
    private final UserProfileService userProfileService;
    private final IntentDetectorService intentDetectorService;
    private final NameFinderME dateFinder;
    private final TelegramClient telegramClient;

    private static final String TEMP_TITLE = "event_title";
    private static final String TEMP_START = "event_start";
    private static final String TEMP_LANGUAGE = "event_language";

    @Value("${event.default.hour:9}")
    private int defaultHour;

    @Value("${event.default.minute:0}")
    private int defaultMinute;

    @Value("${event.default.duration:60}")
    private int defaultDurationMinutes;

    // Patterns for time extraction
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(\\d{1,2})(?::(\\d{2}))?\\s*([AaPp][Mm])?|" +
                    "([AaPp][Mm])\\s*(\\d{1,2})(?::(\\d{2}))?");

    // Relative date keywords (English)
    private static final Map<String, Integer> RELATIVE_DAYS_EN = Map.of(
            "today", 0,
            "tomorrow", 1,
            "day after tomorrow", 2
    );

    // Relative date keywords (Ukrainian)
    private static final Map<String, Integer> RELATIVE_DAYS_UK = Map.of(
            "сьогодні", 0,
            "завтра", 1,
            "післязавтра", 2
    );

    @Override
    public SendMessage handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();

        UserState currentState = stateManager.getState(chatId);
        Language language = detectLanguage(chatId, text);

        return switch (currentState) {
            case IDLE -> startCreateEvent(chatId, language);
            case CREATE_EVENT_AWAITING_TITLE -> handleTitleInput(chatId, text, language);
            case CREATE_EVENT_AWAITING_START -> handleStartInput(chatId, text, language);
            case CREATE_EVENT_AWAITING_END -> handleEndInput(chatId, text, language);
            default -> {
                stateManager.resetState(chatId);
                yield startCreateEvent(chatId, language);
            }
        };
    }

    private Language detectLanguage(Long chatId, String text) {
        // First try profile language
        if (userProfileService.profileExists(chatId)) {
            return userProfileService.getLanguage(chatId);
        }
        // Then detect from text
        String[] tokens = text.split("\\s+");
        return intentDetectorService.detectLanguage(tokens);
    }

    private SendMessage startCreateEvent(Long chatId, Language language) {
        stateManager.setState(chatId, UserState.CREATE_EVENT_AWAITING_TITLE);
        stateManager.setTemporaryData(chatId, TEMP_LANGUAGE, language.name());

        String message = language == Language.UKRAINIAN
                ? "📅 Давайте створимо подію!\n\nЯка буде назва події?"
                : "📅 Let's create an event!\n\nWhat will be the event title?";

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleTitleInput(Long chatId, String title, Language language) {
        if (title.isBlank() || title.length() > 500) {
            String message = language == Language.UKRAINIAN
                    ? "Будь ласка, введіть коректну назву події (1-500 символів):"
                    : "Please enter a valid event title (1-500 characters):";
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
        }

        stateManager.setTemporaryData(chatId, TEMP_TITLE, title);
        stateManager.setState(chatId, UserState.CREATE_EVENT_AWAITING_START);

        String message = language == Language.UKRAINIAN
                ? "Чудово! Коли починається подія?\n\n" +
                  "Приклади: \"завтра о 14:00\", \"сьогодні о 9:00\", \"25.03.2026 10:00\""
                : "Great! When does the event start?\n\n" +
                  "Examples: \"tomorrow at 2:00 PM\", \"today at 9:00\", \"25.03.2026 10:00\"";

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleStartInput(Long chatId, String text, Language language) {
        LocalDateTime startDateTime = extractDateTime(text, language);

        if (startDateTime == null) {
            String message = language == Language.UKRAINIAN
                    ? "Не вдалося розпізнати дату/час. Спробуйте ще раз.\n" +
                      "Приклади: \"завтра о 14:00\", \"сьогодні о 9:00\", \"25.03.2026 10:00\""
                    : "Couldn't recognize the date/time. Please try again.\n" +
                      "Examples: \"tomorrow at 2:00 PM\", \"today at 9:00\", \"25.03.2026 10:00\"";
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
        }

        stateManager.setTemporaryData(chatId, TEMP_START, startDateTime.toString());
        stateManager.setState(chatId, UserState.CREATE_EVENT_AWAITING_END);

        String formattedStart = startDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        String message = language == Language.UKRAINIAN
                ? String.format("Початок: %s\n\nКоли закінчується подія?\n\n" +
                  "Приклади: \"о 15:00\", \"через 2 години\", \"завтра о 10:00\"", formattedStart)
                : String.format("Start: %s\n\nWhen does the event end?\n\n" +
                  "Examples: \"at 3:00 PM\", \"in 2 hours\", \"tomorrow at 10:00\"", formattedStart);

        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(message)
                .build();
    }

    private SendMessage handleEndInput(Long chatId, String text, Language language) {
        String startStr = stateManager.getTemporaryData(chatId, TEMP_START);
        LocalDateTime startDateTime = LocalDateTime.parse(startStr);

        LocalDateTime endDateTime = extractDateTime(text, language, startDateTime);

        if (endDateTime == null) {
            String message = language == Language.UKRAINIAN
                    ? "Не вдалося розпізнати дату/час. Спробуйте ще раз.\n" +
                      "Приклади: \"о 15:00\", \"через 2 години\""
                    : "Couldn't recognize the date/time. Please try again.\n" +
                      "Examples: \"at 3:00 PM\", \"in 2 hours\"";
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
        }

        if (endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime)) {
            String message = language == Language.UKRAINIAN
                    ? "Час закінчення повинен бути після часу початку. Спробуйте ще раз:"
                    : "End time must be after start time. Please try again:";
            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
        }

        // Create the event
        String title = stateManager.getTemporaryData(chatId, TEMP_TITLE);

        try {
            Event event = eventService.createEvent(chatId, title, startDateTime, endDateTime);
            log.info("Created event with id: {} for chatId: {}", event.getId(), chatId);

            // Generate and send ICS file
            File icsFile = IcsFileGenerator.generateIcsFile(event);
            sendIcsFile(chatId, icsFile, language);

            // Clean up
            stateManager.resetState(chatId);

            String formattedStart = startDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
            String formattedEnd = endDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            String message = language == Language.UKRAINIAN
                    ? String.format("✅ Подію створено!\n\n" +
                      "📝 Назва: %s\n" +
                      "🕐 Початок: %s\n" +
                      "🕑 Кінець: %s\n\n" +
                      "📎 Надсилаю вам .ics файл для додавання до календаря.", title, formattedStart, formattedEnd)
                    : String.format("✅ Event created!\n\n" +
                      "📝 Title: %s\n" +
                      "🕐 Start: %s\n" +
                      "🕑 End: %s\n\n" +
                      "📎 Sending you the .ics file to add to your calendar.", title, formattedStart, formattedEnd);

            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();

        } catch (Exception e) {
            log.error("Failed to create event for chatId: {}", chatId, e);
            stateManager.resetState(chatId);

            String message = language == Language.UKRAINIAN
                    ? "❌ Помилка створення події: " + e.getMessage()
                    : "❌ Failed to create event: " + e.getMessage();

            return SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(message)
                    .build();
        }
    }

    private void sendIcsFile(Long chatId, File icsFile, Language language) {
        try {
            String caption = language == Language.UKRAINIAN
                    ? "📅 Ваша подія у форматі .ics"
                    : "📅 Your event in .ics format";

            SendDocument sendDocument = SendDocument.builder()
                    .chatId(chatId.toString())
                    .document(new InputFile(icsFile))
                    .caption(caption)
                    .build();

            telegramClient.execute(sendDocument);
            log.info("Sent ICS file to chatId: {}", chatId);

            // Clean up temp file
            if (icsFile.exists()) {
                icsFile.delete();
            }
        } catch (TelegramApiException e) {
            log.error("Failed to send ICS file to chatId: {}", chatId, e);
        }
    }

    private LocalDateTime extractDateTime(String text, Language language) {
        return extractDateTime(text, language, null);
    }

    private LocalDateTime extractDateTime(String text, Language language, LocalDateTime referenceDateTime) {
        LocalDate date = extractDate(text, language);
        LocalTime time = extractTime(text);

        // Handle "in X hours" pattern
        LocalDateTime durationBased = extractDurationBased(text, referenceDateTime);
        if (durationBased != null) {
            return durationBased;
        }

        if (time == null && date == null) {
            return null;
        }

        if (time == null) {
            time = LocalTime.of(defaultHour, defaultMinute);
        }

        if (date == null) {
            if (referenceDateTime != null) {
                // For end time, if only time provided, use the same date as start
                date = referenceDateTime.toLocalDate();
                // If the time is earlier than start time, assume next day
                if (time.isBefore(referenceDateTime.toLocalTime()) || time.equals(referenceDateTime.toLocalTime())) {
                    date = date.plusDays(1);
                }
            } else {
                if (LocalTime.now().isAfter(time)) {
                    date = LocalDate.now().plusDays(1);
                } else {
                    date = LocalDate.now();
                }
            }
        }

        return LocalDateTime.of(date, time);
    }

    private LocalDateTime extractDurationBased(String text, LocalDateTime reference) {
        if (reference == null) {
            reference = LocalDateTime.now();
        }

        String lowerText = text.toLowerCase();

        // Pattern: "in X hours" / "через X годин"
        Pattern hoursPattern = Pattern.compile("(?:in|через)\\s+(\\d+)\\s*(?:hours?|годин[иу]?|год)");
        Matcher hoursMatcher = hoursPattern.matcher(lowerText);
        if (hoursMatcher.find()) {
            int hours = Integer.parseInt(hoursMatcher.group(1));
            return reference.plusHours(hours);
        }

        // Pattern: "in X minutes" / "через X хвилин"
        Pattern minutesPattern = Pattern.compile("(?:in|через)\\s+(\\d+)\\s*(?:minutes?|mins?|хвилин[иу]?|хв)");
        Matcher minutesMatcher = minutesPattern.matcher(lowerText);
        if (minutesMatcher.find()) {
            int minutes = Integer.parseInt(minutesMatcher.group(1));
            return reference.plusMinutes(minutes);
        }

        return null;
    }

    private LocalDate extractDate(String text, Language language) {
        String lowerText = text.toLowerCase();

        // Check Ukrainian relative dates first
        for (Map.Entry<String, Integer> entry : RELATIVE_DAYS_UK.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                return LocalDate.now().plusDays(entry.getValue());
            }
        }

        // Check English relative dates
        for (Map.Entry<String, Integer> entry : RELATIVE_DAYS_EN.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                return LocalDate.now().plusDays(entry.getValue());
            }
        }

        // Try to extract explicit date
        LocalDate explicitDate = parseExplicitDate(text);
        if (explicitDate != null) {
            return explicitDate;
        }

        // Try NER date extraction
        try {
            String[] tokens = text.split("\\s+");
            Span[] dateSpans = dateFinder.find(tokens);
            if (dateSpans.length > 0) {
                String dateStr = Span.spansToStrings(new Span[]{dateSpans[0]}, tokens)[0];
                LocalDate parsedDate = parseDate(dateStr);
                if (parsedDate != null) {
                    return parsedDate;
                }
            }
        } catch (Exception e) {
            log.debug("NER date extraction failed: {}", e.getMessage());
        } finally {
            dateFinder.clearAdaptiveData();
        }

        return null;
    }

    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMMM d, yyyy", java.util.Locale.ENGLISH)
        );

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException ignored) {
            }
        }

        return null;
    }

    private LocalDate parseExplicitDate(String text) {
        Pattern datePattern = Pattern.compile("(\\d{1,2})[./](\\d{1,2})[./](\\d{4})|(\\d{4})-(\\d{1,2})-(\\d{1,2})");
        Matcher matcher = datePattern.matcher(text);

        if (matcher.find()) {
            try {
                if (matcher.group(1) != null) {
                    int day = Integer.parseInt(matcher.group(1));
                    int month = Integer.parseInt(matcher.group(2));
                    int year = Integer.parseInt(matcher.group(3));
                    return LocalDate.of(year, month, day);
                } else {
                    int year = Integer.parseInt(matcher.group(4));
                    int month = Integer.parseInt(matcher.group(5));
                    int day = Integer.parseInt(matcher.group(6));
                    return LocalDate.of(year, month, day);
                }
            } catch (Exception e) {
                log.debug("Failed to parse explicit date: {}", e.getMessage());
            }
        }

        return null;
    }

    private LocalTime extractTime(String text) {
        Matcher matcher = TIME_PATTERN.matcher(text);

        if (matcher.find()) {
            try {
                int hour;
                int minute = 0;
                String amPm = null;

                if (matcher.group(1) != null) {
                    hour = Integer.parseInt(matcher.group(1));
                    if (matcher.group(2) != null) {
                        minute = Integer.parseInt(matcher.group(2));
                    }
                    amPm = matcher.group(3);
                } else {
                    amPm = matcher.group(4);
                    hour = Integer.parseInt(matcher.group(5));
                    if (matcher.group(6) != null) {
                        minute = Integer.parseInt(matcher.group(6));
                    }
                }

                if (amPm != null) {
                    amPm = amPm.toUpperCase();
                    if ("PM".equals(amPm) && hour != 12) {
                        hour += 12;
                    } else if ("AM".equals(amPm) && hour == 12) {
                        hour = 0;
                    }
                }

                if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                    return LocalTime.of(hour, minute);
                }
            } catch (Exception e) {
                log.debug("Failed to parse time: {}", e.getMessage());
            }
        }

        // Ukrainian time pattern: "о 14:00" or "о 9 годині"
        Pattern ukTimePattern = Pattern.compile("о\\s+(\\d{1,2})(?::(\\d{2}))?(?:\\s*годин[іи])?");
        Matcher ukMatcher = ukTimePattern.matcher(text.toLowerCase());
        if (ukMatcher.find()) {
            try {
                int hour = Integer.parseInt(ukMatcher.group(1));
                int minute = ukMatcher.group(2) != null ? Integer.parseInt(ukMatcher.group(2)) : 0;
                if (hour >= 0 && hour <= 23 && minute >= 0 && minute <= 59) {
                    return LocalTime.of(hour, minute);
                }
            } catch (Exception e) {
                log.debug("Failed to parse Ukrainian time: {}", e.getMessage());
            }
        }

        return null;
    }
}


