
package com.ivan.bot.builder.impl;

import com.ivan.bot.builder.RequestBuilder;
import com.ivan.bot.dto.request.BotRequest;
import com.ivan.bot.dto.request.ReminderRequest;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.service.IntentDetectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
@Component("reminderRequestBuilder")
public class ReminderRequestBuilder implements RequestBuilder {

    private final NameFinderME dateFinder;
    private final IntentDetectorService intentDetectorService;

    @Value("${reminder.default.hour:9}")
    private int defaultHour;

    @Value("${reminder.default.minute:0}")
    private int defaultMinute;

    // Patterns for time extraction (e.g., "9:00", "14:30", "9 AM", "9:00 AM")
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(\\d{1,2})(?::(\\d{2}))?\\s*([AaPp][Mm])?|" +
                    "([AaPp][Mm])\\s*(\\d{1,2})(?::(\\d{2}))?");

    // Keywords for reminder intent removal
    private static final Set<String> REMINDER_KEYWORDS = Set.of(
            "remind", "reminder", "reminders", "notification", "notify", "alert", "schedule",
            "нагадай", "нагадати", "нагадування", "нагадувати", "сповіщення", "сповісти", "сповістити",
            "me", "мені", "about", "про", "at", "о", "on", "в", "за", "tomorrow", "завтра", "today", "сьогодні"
    );

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
    public BotRequest buildRequest(String originalText, String[] tokens, Long chatId) {
        Language detectedLanguage = intentDetectorService.detectLanguage(tokens);

        LocalDateTime reminderDateTime = extractDateTime(tokens, originalText, detectedLanguage);
        String reminderText = extractReminderText(tokens, originalText);

        log.info("Built reminder request: dateTime={}, text={}", reminderDateTime, reminderText);

        return ReminderRequest.builder()
                .chatId(chatId)
                .reminderDateTime(reminderDateTime)
                .reminderText(reminderText)
                .detectedLanguage(detectedLanguage)
                .build();
    }

    private LocalDateTime extractDateTime(String[] tokens, String originalText, Language language) {
        LocalDate date = extractDate(tokens, originalText, language);
        LocalTime time = extractTime(originalText);

        if (date == null) {
            date = LocalDate.now().plusDays(1); // Default to tomorrow
        }

        if (time == null) {
            time = LocalTime.of(defaultHour, defaultMinute); // Default time
        }

        return LocalDateTime.of(date, time);
    }

    private LocalDate extractDate(String[] tokens, String originalText, Language language) {
        String lowerText = originalText.toLowerCase();

        for (Map.Entry<String, Integer> entry : RELATIVE_DAYS_UK.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                return LocalDate.now().plusDays(entry.getValue());
            }
        }

        for (Map.Entry<String, Integer> entry : RELATIVE_DAYS_EN.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                return LocalDate.now().plusDays(entry.getValue());
            }
        }

        try {
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

        LocalDate explicitDate = parseExplicitDate(originalText);
        if (explicitDate != null) {
            return explicitDate;
        }

        return null;
    }

    private LocalDate parseDate(String dateStr) {
        List<DateTimeFormatter> formatters = List.of(
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("d MMMM", Locale.ENGLISH)
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

        return null; // Will use default time
    }

    private String extractReminderText(String[] tokens, String originalText) {
        StringBuilder result = new StringBuilder();
        String lowerText = originalText.toLowerCase();

        // Find the "about" or "про" keyword and extract text after it
        int aboutIndex = lowerText.indexOf(" about ");
        if (aboutIndex != -1) {
            String afterAbout = originalText.substring(aboutIndex + 7).trim();
            return cleanReminderText(afterAbout);
        }

        int proIndex = lowerText.indexOf(" про ");
        if (proIndex != -1) {
            String afterPro = originalText.substring(proIndex + 5).trim();
            return cleanReminderText(afterPro);
        }

        // If no "about/про" found, try to extract meaningful text by removing keywords
        for (String token : originalText.split("\\s+")) {
            String lowerToken = token.toLowerCase();
            if (!REMINDER_KEYWORDS.contains(lowerToken) && !isTimeOrDate(lowerToken)) {
                if (result.length() > 0) {
                    result.append(" ");
                }
                result.append(token);
            }
        }

        String extracted = result.toString().trim();
        return extracted.isEmpty() ? "Reminder" : extracted;
    }

    private String cleanReminderText(String text) {
        // Remove time patterns from reminder text
        String cleaned = text.replaceAll("(?i)at\\s+\\d{1,2}(:\\d{2})?\\s*([AaPp][Mm])?", "")
                .replaceAll("о\\s+\\d{1,2}(:\\d{2})?(?:\\s*годин[іи])?", "")
                .replaceAll("\\d{1,2}[./]\\d{1,2}[./]\\d{4}", "")
                .replaceAll("\\d{4}-\\d{1,2}-\\d{1,2}", "")
                .trim();
        return cleaned.isEmpty() ? "Reminder" : cleaned;
    }

    private boolean isTimeOrDate(String token) {
        // Check if token looks like time or date
        return token.matches("\\d{1,2}(:\\d{2})?") ||
                token.matches("\\d{1,2}[./]\\d{1,2}([./]\\d{2,4})?") ||
                token.matches("(?i)[ap]m") ||
                token.matches("(?i)годин[іи]?");
    }
}

