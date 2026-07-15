package com.yourstore.pattern.observer;

import com.yourstore.entity.Order;
import com.yourstore.enums.OrderStatus;
import org.springframework.context.ApplicationEvent;

public class OrderStatusChangedEvent extends ApplicationEvent {

    private final Order order;
    private final OrderStatus oldStatus;
    private final OrderStatus newStatus;

    // ✅ FIXED: Constructor with 4 parameters (source, order, oldStatus, newStatus)
    public OrderStatusChangedEvent(Object source, Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        super(source);
        this.order = order;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    // ✅ ADD: Convenience constructor for backward compatibility
    public OrderStatusChangedEvent(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        super(order);
        this.order = order;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Order getOrder() {
        return order;
    }

    public OrderStatus getOldStatus() {
        return oldStatus;
    }

    public OrderStatus getNewStatus() {
        return newStatus;
    }
}