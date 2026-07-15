package com.yourstore.controller;

import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.User;
import com.yourstore.pattern.facade.CheckoutFacade;
import com.yourstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;
    private final UserService userService;

    public CheckoutController(CheckoutFacade checkoutFacade, UserService userService) {
        this.checkoutFacade = checkoutFacade;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @PostMapping
    public ResponseEntity<OrderResponse> checkout(@Valid @RequestBody OrderRequest request) {
        User user = getCurrentUser();
        OrderResponse response = checkoutFacade.checkout(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}