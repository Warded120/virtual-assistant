package com.ivan.bot.repository;

import com.ivan.bot.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByChatId(Long chatId);

    List<Event> findByChatIdAndNotifiedFalse(Long chatId);

    @Query("SELECT e FROM Event e WHERE e.startDateTime >= :startTime AND e.startDateTime < :endTime AND e.notified = false")
    List<Event> findDueEvents(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT e FROM Event e WHERE e.chatId = :chatId AND e.startDateTime >= :startDate AND e.startDateTime < :endDate ORDER BY e.startDateTime ASC")
    List<Event> findByChatIdAndDateRange(@Param("chatId") Long chatId,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM Event e WHERE e.chatId = :chatId AND e.startDateTime >= :now ORDER BY e.startDateTime ASC")
    List<Event> findUpcomingEvents(@Param("chatId") Long chatId, @Param("now") LocalDateTime now);

    void deleteByChatIdAndId(Long chatId, Long id);
}

