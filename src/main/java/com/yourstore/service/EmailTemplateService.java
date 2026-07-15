package com.yourstore.service;

import com.yourstore.entity.Order;
import com.yourstore.entity.User;

public interface EmailTemplateService {

    String buildOrderEmailContent(Order order, String templateName);

    String buildWelcomeEmailContent(User user);

    String buildPasswordResetEmailContent(String email, String resetToken);

    String buildAccountDeletedEmailContent(String email, String name);

    String buildLowStockEmailContent(Long productId);

    String buildAutomationFailureEmailContent(String errorDetails);
}