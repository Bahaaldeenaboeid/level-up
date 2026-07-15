package com.yourstore.entity;

import com.yourstore.enums.NotificationType;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "email_sent")
    private Boolean emailSent = false;

    @Column(name = "email_failed")
    private Boolean emailFailed = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Private constructor for Builder

    // Private constructor for Builder
    private Notification(Builder builder) {
        this.user = builder.user;
        this.order = builder.order;
        this.type = builder.type;
        this.title = builder.title;
        this.message = builder.message;
        this.isRead = builder.isRead;
        this.emailSent = builder.emailSent;
        this.emailFailed = builder.emailFailed;
        this.createdAt = builder.createdAt;
    }

    public Notification() {

    }

    // Builder static inner class
    public static class Builder {
        private User user;
        private Order order;
        private NotificationType type;
        private String title;
        private String message;
        private Boolean isRead = false;
        private Boolean emailSent = false;
        private Boolean emailFailed = false;
        private LocalDateTime createdAt;

        public Builder user(User user) { this.user = user; return this; }
        public Builder order(Order order) { this.order = order; return this; }
        public Builder type(NotificationType type) { this.type = type; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public Builder emailSent(Boolean emailSent) { this.emailSent = emailSent; return this; }
        public Builder emailFailed(Boolean emailFailed) { this.emailFailed = emailFailed; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notification build() {
            return new Notification(this);
        }
    }

    // Static factory method
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public Boolean getEmailSent() { return emailSent; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }

    public Boolean getEmailFailed() { return emailFailed; }
    public void setEmailFailed(Boolean emailFailed) { this.emailFailed = emailFailed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isOrderRelated() {
        return order != null;
    }

    public void markAsRead() {
        this.isRead = true;
    }
}