package com.yourstore.dto.request;

import jakarta.validation.constraints.NotNull;

public class NotificationReadRequest {

    @NotNull(message = "Notification ID is required")
    private Long notificationId;

    // Getters and Setters
    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }
}