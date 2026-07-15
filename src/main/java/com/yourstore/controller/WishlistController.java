package com.yourstore.controller;

import com.yourstore.dto.request.WishlistRequest;
import com.yourstore.dto.response.WishlistResponse;
import com.yourstore.entity.User;
import com.yourstore.service.UserService;
import com.yourstore.service.WishlistService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final UserService userService;

    public WishlistController(WishlistService wishlistService, UserService userService) {
        this.wishlistService = wishlistService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public ResponseEntity<Page<WishlistResponse>> getWishlist(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(wishlistService.getWishlist(user, pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<WishlistResponse> addToWishlist(@Valid @RequestBody WishlistRequest request) {
        User user = getCurrentUser();
        WishlistResponse response = wishlistService.addToWishlist(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId) {
        User user = getCurrentUser();
        wishlistService.removeFromWishlist(user, productId);
        return ResponseEntity.noContent().build();
    }
}