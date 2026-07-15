package com.yourstore.mapper;

import com.yourstore.dto.response.AdminCustomerResponse;
import com.yourstore.dto.response.AdminOrderResponse;
import com.yourstore.dto.response.AdminReportResponse;
import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AdminMapper {

    public AdminOrderResponse toOrderResponse(Order order) {
        if (order == null) {
            return null;
        }
        AdminOrderResponse response = new AdminOrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser() != null ? order.getUser().getId() : null);
        response.setCustomerName(order.getUser() != null ? order.getUser().getName() : null);
        response.setCustomerEmail(order.getUser() != null ? order.getUser().getEmail() : null);
        response.setCustomerPhone(order.getUser() != null ? order.getUser().getPhone() : null);
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
        response.setItemCount(order.getTotalItems());
        return response;
    }

    public List<AdminOrderResponse> toOrderResponseList(List<Order> orders) {
        if (orders == null) {
            return new ArrayList<>();
        }
        return orders.stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public AdminCustomerResponse toCustomerResponse(User user, Integer orderCount, BigDecimal totalSpent) {
        if (user == null) {
            return null;
        }
        AdminCustomerResponse response = new AdminCustomerResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setOrderCount(orderCount != null ? orderCount : 0);
        response.setTotalSpent(totalSpent != null ? totalSpent : BigDecimal.ZERO);
        return response;
    }

    public AdminReportResponse toReportResponse(String reportType, LocalDate startDate, LocalDate endDate,
                                                Long orderCount, BigDecimal totalRevenue,
                                                List<AdminReportResponse.DailySalesItem> breakdown) {
        AdminReportResponse response = new AdminReportResponse();
        response.setReportType(reportType);
        response.setStartDate(startDate);
        response.setEndDate(endDate);
        response.setOrderCount(orderCount != null ? orderCount : 0L);
        response.setTotalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        response.setBreakdown(breakdown != null ? breakdown : new ArrayList<>());

        if (orderCount != null && orderCount > 0 && totalRevenue != null) {
            response.setAverageOrderValue(totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP));
        } else {
            response.setAverageOrderValue(BigDecimal.ZERO);
        }
        return response;
    }
}