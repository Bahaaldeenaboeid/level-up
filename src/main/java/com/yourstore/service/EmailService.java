package com.yourstore.service;

public interface EmailService {

    void sendOrderConfirmation(Long orderId);

    void sendOrderProcessingEmail(Long orderId);

    void sendOrderReadyEmail(Long orderId);

    void sendOrderShippedEmail(Long orderId);

    void sendPaymentFailedEmail(Long orderId);

    void sendWelcomeEmail(Long userId);

    void sendPasswordResetEmail(String email, String resetToken);

    void sendAccountDeletedEmail(String email, String name);

    void sendLowStockAlert(Long productId);

    void sendAutomationFailureAlert(String errorDetails);
}