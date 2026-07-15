package com.yourstore.pattern.observer;

import com.yourstore.entity.Order;
import com.yourstore.enums.OrderStatus;
import com.yourstore.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class EmailListener {

    private static final Logger logger = LoggerFactory.getLogger(EmailListener.class);

    private final EmailService emailService;

    public EmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        OrderStatus newStatus = event.getNewStatus();
        logger.info("Handling order status changed event for order {} -> {}", order.getId(), newStatus);
        switch (newStatus) {
            case PROCESSING:
                emailService.sendOrderProcessingEmail(order.getId());
                break;
            case READY:
                emailService.sendOrderReadyEmail(order.getId());
                break;
            case SHIPPED:
                emailService.sendOrderShippedEmail(order.getId());
                break;
            default:
                logger.debug("No email action configured for order status {}", newStatus);
        }
    }
}
