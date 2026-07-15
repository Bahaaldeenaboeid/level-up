package com.yourstore.service.impl;

import com.yourstore.dto.response.NotificationResponse;
import com.yourstore.entity.Notification;
import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import com.yourstore.enums.NotificationType;
import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.mapper.NotificationMapper;
import com.yourstore.repository.NotificationRepository;
import com.yourstore.repository.UserRepository;
import com.yourstore.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    private static final Long ADMIN_USER_ID = 1L;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public void createNotification(User user, Order order, NotificationType type, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .order(order)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .emailSent(false)
                .emailFailed(false)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public void createNotificationForAdmin(NotificationType type, String title, String message) {
        User admin = userRepository.findById(ADMIN_USER_ID)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        Notification notification = Notification.builder()
                .user(admin)
                .order(null)
                .type(type)
                .title(title)
                .message(message)
                .isRead(false)
                .emailSent(false)
                .emailFailed(false)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public Page<NotificationResponse> getUserNotifications(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, pageable)
                .map(notificationMapper::toResponse);
    }

    @Override
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("You do not have permission to modify this notification");
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(User user) {
        // ✅ FIXED: Use the new method name
        notificationRepository.markAllAsReadByUser(user);
    }

    @Override
    public void deleteNotification(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new InvalidRequestException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    @Override
    public long countUnread(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}