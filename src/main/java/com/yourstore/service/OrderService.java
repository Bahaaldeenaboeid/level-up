package com.yourstore.service;

import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.request.OrderStatusUpdateRequest;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import com.yourstore.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(User user, OrderRequest request);

    OrderResponse getOrderById(Long orderId, User user);

    List<OrderResponse> getUserOrders(User user);

    Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable);

    OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request);

    void confirmPayment(Long orderId);  // ✅ ADD THIS

    Order findOrderById(Long orderId);

    List<Order> getReadyOrdersForBatch();

    void markOrdersAsShipped(List<Order> orders);

    OrderResponse getOrderForTracking(Long orderId, User user);
}