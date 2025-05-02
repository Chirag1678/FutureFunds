package com.cg.futurefunds.service;

import com.cg.futurefunds.exceptions.FutureFundsException;
import com.cg.futurefunds.model.Notification;
import com.cg.futurefunds.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationScheduler {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Scheduled(fixedRate = 60000)
    public void scheduleNotifications() {
        List<Notification> notifications = notificationRepository.findByScheduledAtLessThanEqual(LocalDateTime.now());

        for(Notification notification: notifications) {
            try {
                notificationService.sendNotification(notification.getId());
            } catch (Exception e) {
                System.err.println("Error sending notification ID " + notification.getId() + ": " + e.getMessage());
            }
        }
    }
}
