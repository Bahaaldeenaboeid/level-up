package com.yourstore.pattern.state;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.entity.Order;
import com.yourstore.enums.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Set;

@Component
public class OrderStateMachine {

    private static final Logger logger = LoggerFactory.getLogger(OrderStateMachine.class);

    private final EnumMap<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);

    public OrderStateMachine() {
        transitions.put(OrderStatus.NEW, EnumSet.of(OrderStatus.PROCESSING));
        transitions.put(OrderStatus.PROCESSING, EnumSet.of(OrderStatus.READY));
        transitions.put(OrderStatus.READY, EnumSet.of(OrderStatus.SHIPPED));
        transitions.put(OrderStatus.SHIPPED, Collections.emptySet());
    }

    public void transition(Order order, OrderStatus newStatus) {
        OrderStatus currentStatus = order.getStatus();
        logger.info("Attempting state transition for order {} from {} to {}", order.getId(), currentStatus, newStatus);

        if (!canTransition(currentStatus, newStatus)) {
            String message = String.format("Cannot transition order %d from %s to %s", order.getId(), currentStatus, newStatus);
            logger.warn(message);
            throw new InvalidRequestException(message);
        }

        // ✅ REMOVED: order.setStatus(newStatus); — Service sets the status
        logger.info("State transition validated for order {} from {} to {}", order.getId(), currentStatus, newStatus);
    }

    public boolean canTransition(OrderStatus current, OrderStatus target) {
        Set<OrderStatus> validTargets = transitions.get(current);
        return validTargets != null && validTargets.contains(target);
    }
}