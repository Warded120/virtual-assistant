package com.ivan.bot.service;

import com.ivan.bot.entity.Event;
import com.ivan.bot.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public Event createEvent(Long chatId, String title, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        log.info("Creating event for chatId: {} from {} to {}", chatId, startDateTime, endDateTime);

        Event event = Event.builder()
                .chatId(chatId)
                .title(title)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .notified(false)
                .build();

        return eventRepository.save(event);
    }

    public List<Event> getEvents(Long chatId) {
        return eventRepository.findByChatId(chatId);
    }

    public List<Event> getUpcomingEvents(Long chatId) {
        return eventRepository.findUpcomingEvents(chatId, LocalDateTime.now());
    }

    public List<Event> getEventsForDate(Long chatId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return eventRepository.findByChatIdAndDateRange(chatId, startOfDay, endOfDay);
    }

    public List<Event> getEventsInRange(Long chatId, LocalDateTime start, LocalDateTime end) {
        return eventRepository.findByChatIdAndDateRange(chatId, start, end);
    }

    public Optional<Event> getEvent(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getDueEvents(LocalDateTime startTime, LocalDateTime endTime) {
        return eventRepository.findDueEvents(startTime, endTime);
    }

    @Transactional
    public void markAsNotified(Long eventId) {
        eventRepository.findById(eventId).ifPresent(event -> {
            event.setNotified(true);
            eventRepository.save(event);
            log.info("Marked event {} as notified", eventId);
        });
    }

    @Transactional
    public void deleteEvent(Long chatId, Long eventId) {
        eventRepository.deleteByChatIdAndId(chatId, eventId);
        log.info("Deleted event {} for chatId {}", eventId, chatId);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        eventRepository.deleteById(eventId);
        log.info("Deleted event {}", eventId);
    }
}

