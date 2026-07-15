package com.yourstore.controller;

import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.User;
import com.yourstore.enums.OrderStatus;
import com.yourstore.service.OrderService;
import com.yourstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderService.getUserOrders(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderService.getOrderById(id, user));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        User user = getCurrentUser();
        OrderResponse response = orderService.createOrder(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/track/{orderId}")
    public ResponseEntity<OrderResponse> trackOrder(@PathVariable Long orderId) {
        User user = getCurrentUser();
        return ResponseEntity.ok(orderService.getOrderForTracking(orderId, user));
    }

    @GetMapping("/next-shipping-date")
    public ResponseEntity<String> getNextShippingDate() {
        // Returns next shipping date (every 2 days at 06:00)
        return ResponseEntity.ok("Next shipping date: " + /* calculate next date */ "2024-07-10 06:00 AST");
    }
}