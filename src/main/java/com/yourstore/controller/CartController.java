package com.yourstore.controller;

import com.yourstore.dto.request.BulkCartRequest;
import com.yourstore.dto.request.CartAddRequest;
import com.yourstore.dto.response.CartResponse;
import com.yourstore.entity.User;
import com.yourstore.service.CartService;
import com.yourstore.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            logger.error("No authenticated user found in security context");
            throw new RuntimeException("User not authenticated");
        }
        String email = auth.getName();
        logger.debug("Fetching user by email: {}", email);
        return userService.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        User user = getCurrentUser();
        return ResponseEntity.ok(cartService.getCart(user));
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody CartAddRequest request) {
        User user = getCurrentUser();
        logger.info("Adding product {} to cart for user {}", request.getProductId(), user.getEmail());
        return ResponseEntity.ok(cartService.addToCart(user, request));
    }

    @PostMapping("/add-bulk")
    public ResponseEntity<CartResponse> addMultipleToCart(@Valid @RequestBody BulkCartRequest request) {
        User user = getCurrentUser();
        logger.info("Adding {} items to cart for user {}", request.getItems().size(), user.getEmail());
        return ResponseEntity.ok(cartService.addMultipleToCart(user, request));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        User user = getCurrentUser();
        logger.info("Updating quantity for product {} to {} for user {}", productId, quantity, user.getEmail());
        return ResponseEntity.ok(cartService.updateQuantity(user, productId, quantity));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long productId) {
        User user = getCurrentUser();
        logger.info("Removing product {} from cart for user {}", productId, user.getEmail());
        return ResponseEntity.ok(cartService.removeFromCart(user, productId));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearCart() {
        User user = getCurrentUser();
        logger.info("Clearing cart for user {}", user.getEmail());
        cartService.clearCart(user);
        return ResponseEntity.noContent().build();
    }
}