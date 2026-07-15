package com.yourstore.service;

import com.yourstore.dto.response.NotificationResponse;
import com.yourstore.entity.Notification;
import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import com.yourstore.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(User user, Order order, NotificationType type, String title, String message);

    void createNotificationForAdmin(NotificationType type, String title, String message);

    Page<NotificationResponse> getUserNotifications(User user, Pageable pageable);

    void markAsRead(Long notificationId, User user);

    void markAllAsRead(User user);

    void deleteNotification(Long notificationId, User user);

    long countUnread(User user);
}