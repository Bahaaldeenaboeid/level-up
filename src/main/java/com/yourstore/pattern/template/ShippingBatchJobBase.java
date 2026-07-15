package com.yourstore.pattern.template;

import com.yourstore.entity.Order;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class ShippingBatchJobBase {

    public final void run() {
        List<Order> readyOrders = fetchReadyOrders();
        Map<String, List<Order>> ordersByCity = groupByCity(readyOrders);
        generateLabels(ordersByCity);
        updateStatus(readyOrders);
        notifyCustomers(readyOrders);
    }

    protected List<Order> fetchReadyOrders() {
        return getReadyOrders();
    }

    protected Map<String, List<Order>> groupByCity(List<Order> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(order -> order.getCity() != null ? order.getCity().getName() : "Unknown"));
    }

    protected void updateStatus(List<Order> orders) {
        for (Order order : orders) {
            order.setStatus(com.yourstore.enums.OrderStatus.SHIPPED);
        }
    }

    protected abstract List<Order> getReadyOrders();

    protected abstract void generateLabels(Map<String, List<Order>> ordersByCity);

    protected abstract void notifyCustomers(List<Order> orders);
}
