package com.cg.futurefunds.service;

import com.cg.futurefunds.dto.NotificationDTO;
import com.cg.futurefunds.dto.ResponseDTO;

import java.util.List;

public interface NotificationService {
    ResponseDTO createNotification(NotificationDTO notificationDTO);
    ResponseDTO updateNotification(Long notificationId, NotificationDTO notificationDTO);
    ResponseDTO deleteNotification(Long notificationId);
    ResponseDTO sendNotification(Long notificationId);
    ResponseDTO sendPdfNotification(String to, String filePath);
    ResponseDTO getAllNotifications(Long userId);
}
