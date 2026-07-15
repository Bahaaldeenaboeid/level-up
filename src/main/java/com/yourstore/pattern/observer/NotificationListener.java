package com.yourstore.pattern.observer;

import com.yourstore.entity.Notification;
import com.yourstore.entity.Order;
import com.yourstore.enums.NotificationType;
import com.yourstore.enums.OrderStatus;
import com.yourstore.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationRepository notificationRepository;

    public NotificationListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        OrderStatus newStatus = event.getNewStatus();
        logger.info("Creating notification for order {} status change to {}", order.getId(), newStatus);

        NotificationType type;
        String title;
        String message;

        switch (newStatus) {
            case PROCESSING:
                type = NotificationType.PROCESSING;
                title = "Order Processing";
                message = "Your order #" + order.getId() + " is being processed.";
                break;
            case READY:
                type = NotificationType.READY;
                title = "Order Ready";
                message = "Your order #" + order.getId() + " is ready for shipment.";
                break;
            case SHIPPED:
                type = NotificationType.SHIPPED;
                title = "Order Shipped";
                message = "Your order #" + order.getId() + " has been shipped and is being delivered today.";
                break;
            default:
                logger.debug("No notification created for order status {}", newStatus);
                return;
        }

        Notification notification = Notification.builder()
                .user(order.getUser())
                .order(order)
                .type(type)
                .title(title)
                .message(message)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        logger.info("Saved notification for order {} with status {}", order.getId(), newStatus);
    }
}