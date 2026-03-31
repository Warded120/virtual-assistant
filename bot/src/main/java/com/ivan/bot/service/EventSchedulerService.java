package com.ivan.bot.service;

import com.ivan.bot.entity.Event;
import com.ivan.bot.enumeration.Language;
import com.ivan.bot.util.IcsFileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventSchedulerService {

    private final EventService eventService;
    private final TelegramClient telegramClient;
    private final UserProfileService userProfileService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Scheduled(fixedRateString = "${event.scheduler.fixedRate:PT1M}")
    public void checkAndSendEventNotifications() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMinute = now.withSecond(0).withNano(0);
        LocalDateTime endOfMinute = startOfMinute.plusMinutes(1);

        log.debug("Checking for due events between {} and {}", startOfMinute, endOfMinute);

        List<Event> dueEvents = eventService.getDueEvents(startOfMinute, endOfMinute);

        if (!dueEvents.isEmpty()) {
            log.info("Found {} due events", dueEvents.size());
        }

        for (Event event : dueEvents) {
            sendEventNotification(event);
        }
    }

    private void sendEventNotification(Event event) {
        try {
            String message = buildEventMessage(event);
            Language language = userProfileService.getLanguage(event.getChatId());

            // Send notification message
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(event.getChatId().toString())
                    .text(message)
                    .build();

            telegramClient.execute(sendMessage);

            // Send ICS file
            try {
                File icsFile = IcsFileGenerator.generateIcsFile(event);
                String caption = language == Language.UKRAINIAN
                        ? "📅 Ваша подія у форматі .ics"
                        : "📅 Your event in .ics format";

                SendDocument sendDocument = SendDocument.builder()
                        .chatId(event.getChatId().toString())
                        .document(new InputFile(icsFile))
                        .caption(caption)
                        .build();

                telegramClient.execute(sendDocument);

                // Clean up temp file
                if (icsFile.exists()) {
                    icsFile.delete();
                }
            } catch (Exception e) {
                log.error("Failed to send ICS file for event id: {}", event.getId(), e);
            }

            eventService.markAsNotified(event.getId());

            log.info("Sent event notification to chatId: {} for event: {}",
                    event.getChatId(), event.getId());

        } catch (TelegramApiException e) {
            log.error("Failed to send event notification for event id: {}", event.getId(), e);
        }
    }

    private String buildEventMessage(Event event) {
        Language language = userProfileService.getLanguage(event.getChatId());

        String formattedStart = event.getStartDateTime().format(FORMATTER);
        String formattedEnd = event.getEndDateTime().format(FORMATTER);

        if (language == Language.UKRAINIAN) {
            return String.format(
                    "🔔 Нагадування про подію!\n\n" +
                    "📝 %s\n" +
                    "🕐 Початок: %s\n" +
                    "🕑 Кінець: %s",
                    event.getTitle(),
                    formattedStart,
                    formattedEnd
            );
        }

        return String.format(
                "🔔 Event Reminder!\n\n" +
                "📝 %s\n" +
                "🕐 Start: %s\n" +
                "🕑 End: %s",
                event.getTitle(),
                formattedStart,
                formattedEnd
        );
    }
}

