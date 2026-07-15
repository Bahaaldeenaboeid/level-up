package com.yourstore.service.impl;

import com.yourstore.entity.Order;
import com.yourstore.entity.OrderItem;
import com.yourstore.entity.User;
import com.yourstore.repository.OrderRepository;
import com.yourstore.repository.ProductRepository;
import com.yourstore.repository.UserRepository;
import com.yourstore.service.EmailTemplateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * ✅ Simplified — No Thymeleaf, just plain text building
 * Used by EmailServiceImpl as an alternative (or can be removed completely)
 */
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public EmailTemplateServiceImpl(OrderRepository orderRepository,
                                    UserRepository userRepository,
                                    ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public String buildOrderEmailContent(Order order, String templateName) {
        User user = order.getUser();
        String cityName = order.getCity() != null ? order.getCity().getName() : "Unknown";

        StringBuilder message = new StringBuilder();
        message.append("Order #").append(order.getId()).append("\n");
        message.append("Customer: ").append(user.getName()).append("\n");
        message.append("Email: ").append(user.getEmail()).append("\n");
        message.append("Address: ").append(order.getShippingAddress()).append("\n");
        message.append("City: ").append(cityName).append("\n");
        message.append("Phone: ").append(order.getShippingPhone()).append("\n");
        message.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
        message.append("Payment Status: ").append(order.getPaymentStatus()).append("\n");
        message.append("Order Status: ").append(order.getStatus()).append("\n\n");

        message.append("Items:\n");
        for (OrderItem item : order.getOrderItems()) {
            message.append("  - ")
                    .append(item.getProduct().getName())
                    .append(" x")
                    .append(item.getQuantity())
                    .append(" → ")
                    .append(item.getSubtotal())
                    .append(" SAR\n");
        }

        message.append("\nSubtotal: ").append(calculateSubtotal(order)).append(" SAR\n");
        message.append("Shipping: ").append(order.getShippingCost()).append(" SAR\n");
        message.append("Total: ").append(order.getTotalAmount()).append(" SAR\n\n");
        message.append("Thank you for your order!");

        return message.toString();
    }

    @Override
    public String buildWelcomeEmailContent(User user) {
        return "Welcome to our store, " + user.getName() + "!\n\n"
                + "Thank you for registering. You can now browse and purchase our products.\n\n"
                + "Best regards,\nThe Store Team";
    }

    @Override
    public String buildPasswordResetEmailContent(String email, String resetToken) {
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        return "You requested a password reset.\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link expires in 1 hour.";
    }

    @Override
    public String buildAccountDeletedEmailContent(String email, String name) {
        return "Hello " + name + ",\n\n"
                + "Your account (" + email + ") has been successfully deleted.\n"
                + "All your data has been permanently removed from our system.\n\n"
                + "Best regards,\nThe Store Team";
    }

    @Override
    public String buildLowStockEmailContent(Long productId) {
        String productName = productRepository.findById(productId)
                .map(p -> p.getName())
                .orElse("Unknown Product");

        return "⚠️ Low Stock Alert!\n\n"
                + "Product: " + productName + "\n"
                + "Please restock this product.\n\n"
                + "Best regards,\nThe Store Team";
    }

    @Override
    public String buildAutomationFailureEmailContent(String errorDetails) {
        return "❌ Automation Failure Alert!\n\n"
                + "The automated shipping batch job failed.\n\n"
                + "Error Details:\n" + errorDetails + "\n\n"
                + "Please check the logs and resolve the issue.";
    }

    private BigDecimal calculateSubtotal(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}