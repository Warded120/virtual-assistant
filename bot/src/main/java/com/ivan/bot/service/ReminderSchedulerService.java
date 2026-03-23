package com.ivan.bot.service;

import com.ivan.bot.entity.Reminder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderSchedulerService {

    private final ReminderService reminderService;
    private final TelegramClient telegramClient;
    private final UserProfileService userProfileService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Scheduled(fixedRateString = "${reminder.scheduler.fixedRate:PT1M}")
    public void checkAndSendReminders() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMinute = now.withSecond(0).withNano(0);
        LocalDateTime endOfMinute = startOfMinute.plusMinutes(1);

        log.debug("Checking for due reminders between {} and {}", startOfMinute, endOfMinute);

        List<Reminder> dueReminders = reminderService.getDueReminders(startOfMinute, endOfMinute);

        if (!dueReminders.isEmpty()) {
            log.info("Found {} due reminders", dueReminders.size());
        }

        for (Reminder reminder : dueReminders) {
            sendReminderNotification(reminder);
        }
    }

    private void sendReminderNotification(Reminder reminder) {
        try {
            String message = buildReminderMessage(reminder);

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(reminder.getChatId().toString())
                    .text(message)
                    .build();

            telegramClient.execute(sendMessage);
            reminderService.markAsNotified(reminder.getId());

            log.info("Sent reminder notification to chatId: {} for reminder: {}",
                    reminder.getChatId(), reminder.getId());

        } catch (TelegramApiException e) {
            log.error("Failed to send reminder notification for reminder id: {}", reminder.getId(), e);
        }
    }

    private String buildReminderMessage(Reminder reminder) {
        var language = userProfileService.getLanguage(reminder.getChatId());

        String formattedTime = reminder.getReminderDateTime().format(FORMATTER);

        if (language == com.ivan.bot.enumeration.Language.UKRAINIAN) {
            return String.format(
                    "🔔 Нагадування!\n\n" +
                            "📝 %s\n" +
                            "🕐 Заплановано на: %s",
                    reminder.getReminderText(),
                    formattedTime
            );
        }

        return String.format(
                "🔔 Reminder!\n\n" +
                        "📝 %s\n" +
                        "🕐 Scheduled for: %s",
                reminder.getReminderText(),
                formattedTime
        );
    }
}



