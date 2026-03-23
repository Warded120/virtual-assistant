package com.ivan.bot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "reminder_text", nullable = false)
    private String reminderText;

    @Column(name = "reminder_date_time", nullable = false)
    private LocalDateTime reminderDateTime;

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

