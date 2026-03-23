package com.ivan.bot.repository;

import com.ivan.bot.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByChatId(Long chatId);

    List<Reminder> findByChatIdAndNotifiedFalse(Long chatId);

    @Query("SELECT r FROM Reminder r WHERE r.reminderDateTime >= :startTime AND r.reminderDateTime < :endTime AND r.notified = false")
    List<Reminder> findDueReminders(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    void deleteByChatIdAndId(Long chatId, Long id);
}

