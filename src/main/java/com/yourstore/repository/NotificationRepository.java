package com.yourstore.repository;

import com.yourstore.entity.Notification;
import com.yourstore.entity.User;
import com.yourstore.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a user (most recent first)
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // Find all UNREAD notifications for a user
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    // Find all notifications for a user by type
    List<Notification> findByUserAndType(User user, NotificationType type);

    // Count UNREAD notifications for a user
    long countByUserAndIsReadFalse(User user);

    // ✅ FIXED: Mark all notifications as read for a user
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user AND n.isRead = false")
    void markAllAsReadByUser(@Param("user") User user);

    // Delete all notifications for a user (used when account is deleted)
    void deleteAllByUser(User user);
}