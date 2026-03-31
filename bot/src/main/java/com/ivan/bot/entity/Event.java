package com.ivan.bot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "notified", nullable = false)
    private Boolean notified;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (notified == null) {
            notified = false;
        }
    }
}

