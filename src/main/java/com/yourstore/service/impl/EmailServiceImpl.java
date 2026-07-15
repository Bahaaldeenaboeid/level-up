package com.yourstore.service.impl;

import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.entity.Order;
import com.yourstore.entity.OrderItem;
import com.yourstore.entity.User;
import com.yourstore.repository.OrderRepository;
import com.yourstore.repository.ProductRepository;
import com.yourstore.repository.UserRepository;
import com.yourstore.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public EmailServiceImpl(JavaMailSender mailSender,
                            OrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository) {
        this.mailSender = mailSender;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // ===== ORDER EMAILS =====

    @Override
    public void sendOrderConfirmation(Long orderId) {
        sendOrderEmail(orderId, "✅ Order Confirmation - #" + orderId);
    }

    @Override
    public void sendOrderProcessingEmail(Long orderId) {
        sendOrderEmail(orderId, "🔄 Order Processing - #" + orderId);
    }

    @Override
    public void sendOrderReadyEmail(Long orderId) {
        sendOrderEmail(orderId, "📦 Order Ready for Shipping - #" + orderId);
    }

    @Override
    public void sendOrderShippedEmail(Long orderId) {
        sendOrderEmail(orderId, "🚚 Order Shipped - #" + orderId);
    }

    @Override
    public void sendPaymentFailedEmail(Long orderId) {
        sendOrderEmail(orderId, "❌ Payment Failed - #" + orderId);
    }

    // ===== NON-ORDER EMAILS =====

    @Override
    public void sendWelcomeEmail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String message = "Welcome to Our Store, " + user.getName() + "!\n\n"
                + "Thank you for registering. You can now browse and purchase our products.\n\n"
                + "Best regards,\nThe Store Team";

        sendPlainEmail(user.getEmail(), "🎉 Welcome to Our Store!", message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        String resetLink = "http://localhost:8080/reset-password?token=" + resetToken;
        String message = "You requested a password reset.\n\n"
                + "Click the link below to reset your password:\n"
                + resetLink + "\n\n"
                + "This link expires in 1 hour.\n\n"
                + "If you did not request this, please ignore this email.";

        sendPlainEmail(email, "🔑 Password Reset Request", message);
    }

    @Override
    public void sendAccountDeletedEmail(String email, String name) {
        String message = "Hello " + name + ",\n\n"
                + "Your account (" + email + ") has been successfully deleted.\n"
                + "All your data has been permanently removed from our system.\n\n"
                + "Best regards,\nThe Store Team";

        sendPlainEmail(email, "🗑️ Account Deleted", message);
    }

    // ===== ADMIN EMAILS =====

    @Override
    public void sendLowStockAlert(Long productId) {
        String productName = productRepository.findById(productId)
                .map(p -> p.getName())
                .orElse("Unknown Product");

        String message = "⚠️ LOW STOCK ALERT!\n\n"
                + "Product: " + productName + "\n"
                + "Please restock this product immediately.\n\n"
                + "Best regards,\nThe Store Team";

        User admin = userRepository.findById(1L).orElse(null);
        if (admin != null) {
            sendPlainEmail(admin.getEmail(), "⚠️ Low Stock Alert", message);
        } else {
            logger.warn("Admin user not found — cannot send low stock alert for product {}", productId);
        }
    }

    @Override
    public void sendAutomationFailureAlert(String errorDetails) {
        String message = "❌ AUTOMATION FAILURE ALERT!\n\n"
                + "The automated shipping batch job failed.\n\n"
                + "Error Details:\n" + errorDetails + "\n\n"
                + "Please check the logs and resolve the issue.\n\n"
                + "Best regards,\nThe Store Team";

        User admin = userRepository.findById(1L).orElse(null);
        if (admin != null) {
            sendPlainEmail(admin.getEmail(), "❌ Automation Failure Alert", message);
        } else {
            logger.warn("Admin user not found — cannot send automation failure alert");
        }
    }

    // ===== PRIVATE HELPERS =====

    private void sendOrderEmail(Long orderId, String subject) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

            User user = order.getUser();
            String cityName = order.getCity() != null ? order.getCity().getName() : "Unknown";

            StringBuilder message = new StringBuilder();
            message.append("=").append("=".repeat(50)).append("\n");
            message.append("  ORDER #").append(order.getId()).append("\n");
            message.append("=").append("=".repeat(50)).append("\n\n");

            message.append("📋 CUSTOMER INFORMATION\n");
            message.append("──────────────────────\n");
            message.append("Name:    ").append(user.getName()).append("\n");
            message.append("Email:   ").append(user.getEmail()).append("\n");
            message.append("Phone:   ").append(order.getShippingPhone()).append("\n");
            message.append("Address: ").append(order.getShippingAddress()).append("\n");
            message.append("City:    ").append(cityName).append("\n\n");

            message.append("📦 ORDER DETAILS\n");
            message.append("────────────────\n");
            message.append("Order ID:       ").append(order.getId()).append("\n");
            message.append("Order Date:     ").append(order.getOrderDate()).append("\n");
            message.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
            message.append("Payment Status: ").append(order.getPaymentStatus()).append("\n");
            message.append("Order Status:   ").append(order.getStatus()).append("\n\n");

            message.append("🛒 ITEMS ORDERED\n");
            message.append("────────────────\n");
            for (OrderItem item : order.getOrderItems()) {
                message.append("  • ")
                        .append(item.getProduct().getName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" → ")
                        .append(item.getSubtotal())
                        .append(" SAR\n");
            }
            message.append("\n");

            message.append("💰 ORDER TOTALS\n");
            message.append("───────────────\n");
            message.append("Subtotal:  ").append(calculateSubtotal(order)).append(" SAR\n");
            message.append("Shipping:  ").append(order.getShippingCost()).append(" SAR\n");
            message.append("───────────────\n");
            message.append("TOTAL:     ").append(order.getTotalAmount()).append(" SAR\n\n");

            message.append("=").append("=".repeat(50)).append("\n");
            message.append("Thank you for shopping with us!\n");
            message.append("=").append("=".repeat(50)).append("\n");

            sendPlainEmail(user.getEmail(), subject, message.toString());

            logger.info("✅ Order email sent for order #{} to {}", orderId, user.getEmail());

        } catch (Exception e) {
            logger.error("❌ Failed to send order email for order {}: {}", orderId, e.getMessage());
        }
    }

    private void sendPlainEmail(String to, String subject, String message) {
        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);
            mailSender.send(email);
            logger.info("📧 Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            logger.error("❌ Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private BigDecimal calculateSubtotal(Order order) {
        return order.getOrderItems().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String repeat(String str, int count) {
        return str.repeat(count);
    }
}