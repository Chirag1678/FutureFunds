package com.cg.futurefunds.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@RequiredArgsConstructor
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private LocalDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long investment;
    private Long goal;
    private Long user;
}
