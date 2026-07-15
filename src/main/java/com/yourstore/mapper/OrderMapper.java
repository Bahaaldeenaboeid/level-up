package com.yourstore.mapper;

import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.City;
import com.yourstore.entity.Order;
import com.yourstore.entity.OrderItem;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequest request, User user, City city, BigDecimal totalAmount, BigDecimal shippingCost) {
        if (request == null || user == null || city == null) {
            return null;
        }
        Order order = new Order();
        order.setUser(user);
        order.setCity(city);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingPhone(request.getShippingPhone());
        order.setShippingCost(shippingCost);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setTotalAmount(totalAmount);
        return order;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getUser() != null ? order.getUser().getName() : null);
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setShippingCity(order.getCity() != null ? order.getCity().getName() : null);
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingPhone(order.getShippingPhone());
        response.setShippingCost(order.getShippingCost());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setOrderDate(order.getOrderDate());
        response.setShippingDate(order.getShippingDate());

        // Map order items
        if (order.getOrderItems() != null && !order.getOrderItems().isEmpty()) {
            List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                    .map(this::toOrderItemResponse)
                    .collect(Collectors.toList());
            response.setItems(itemResponses);
        } else {
            response.setItems(new ArrayList<>());
        }

        return response;
    }

    public OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }
        OrderResponse.OrderItemResponse response = new OrderResponse.OrderItemResponse();
        response.setProductId(orderItem.getProduct().getId());
        response.setProductName(orderItem.getProduct().getName());
        response.setProductBrand(orderItem.getProduct().getBrand());
        response.setQuantity(orderItem.getQuantity());
        response.setPriceAtTime(orderItem.getPriceAtTime());
        response.setSubtotal(orderItem.getSubtotal());
        return response;
    }

    public List<OrderResponse> toResponseList(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}