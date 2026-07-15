package com.yourstore.mapper;

import com.yourstore.dto.response.NotificationResponse;
import com.yourstore.entity.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        if (notification == null) {
            return null;
        }
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setIsRead(notification.getIsRead());
        response.setOrderId(notification.getOrder() != null ? notification.getOrder().getId() : null);
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }

    public List<NotificationResponse> toResponseList(List<Notification> notifications) {
        if (notifications == null) {
            return new ArrayList<>();
        }
        return notifications.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}