package com.ivan.bot.service;

import com.ivan.bot.entity.Reminder;
import com.ivan.bot.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    @Transactional
    public Reminder createReminder(Long chatId, String reminderText, LocalDateTime reminderDateTime) {
        log.info("Creating reminder for chatId: {} at {}", chatId, reminderDateTime);

        Reminder reminder = Reminder.builder()
                .chatId(chatId)
                .reminderText(reminderText)
                .reminderDateTime(reminderDateTime)
                .notified(false)
                .build();

        return reminderRepository.save(reminder);
    }

    public List<Reminder> getReminders(Long chatId) {
        return reminderRepository.findByChatId(chatId);
    }

    public List<Reminder> getPendingReminders(Long chatId) {
        return reminderRepository.findByChatIdAndNotifiedFalse(chatId);
    }

    public Optional<Reminder> getReminder(Long id) {
        return reminderRepository.findById(id);
    }

    public List<Reminder> getDueReminders(LocalDateTime startTime, LocalDateTime endTime) {
        return reminderRepository.findDueReminders(startTime, endTime);
    }

    @Transactional
    public void markAsNotified(Long reminderId) {
        reminderRepository.findById(reminderId).ifPresent(reminder -> {
            reminder.setNotified(true);
            reminderRepository.save(reminder);
            log.info("Marked reminder {} as notified", reminderId);
        });
    }

    @Transactional
    public void deleteReminder(Long chatId, Long reminderId) {
        reminderRepository.deleteByChatIdAndId(chatId, reminderId);
        log.info("Deleted reminder {} for chatId {}", reminderId, chatId);
    }

    @Transactional
    public void deleteReminder(Long reminderId) {
        reminderRepository.deleteById(reminderId);
        log.info("Deleted reminder {}", reminderId);
    }
}

