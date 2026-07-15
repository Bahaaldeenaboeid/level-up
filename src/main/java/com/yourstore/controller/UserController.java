package com.yourstore.controller;

import com.yourstore.dto.request.UserDeleteRequest;
import com.yourstore.dto.request.UserProfileRequest;
import com.yourstore.dto.response.NotificationResponse;
import com.yourstore.dto.response.UserProfileResponse;
import com.yourstore.entity.User;
import com.yourstore.service.NotificationService;
import com.yourstore.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;  // ✅ ADDED

    public UserController(UserService userService, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        User user = getCurrentUser();
        return ResponseEntity.ok(userService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UserProfileRequest request) {
        User user = getCurrentUser();
        return ResponseEntity.ok(userService.updateProfile(user.getId(), request));
    }

    @DeleteMapping("/account")
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody UserDeleteRequest request) {
        User user = getCurrentUser();
        userService.deleteAccount(user.getId(), request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications")
    public ResponseEntity<Page<NotificationResponse>> getNotifications(Pageable pageable) {
        User user = getCurrentUser();
        return ResponseEntity.ok(notificationService.getUserNotifications(user, pageable));
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable Long id) {
        User user = getCurrentUser();
        notificationService.markAsRead(id, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notifications/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead() {
        User user = getCurrentUser();
        notificationService.markAllAsRead(user);
        return ResponseEntity.ok().build();
    }
}