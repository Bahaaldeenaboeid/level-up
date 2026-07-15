package com.yourstore.controller;

import com.yourstore.dto.request.ReviewRequest;
import com.yourstore.dto.response.ReviewResponse;
import com.yourstore.entity.User;
import com.yourstore.service.ReviewService;
import com.yourstore.service.UserService;
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
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userService.findByEmail(email);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId, pageable));
    }

    @GetMapping("/product/{productId}/rating/{rating}")
    public ResponseEntity<Page<ReviewResponse>> getReviewsByProductAndRating(
            @PathVariable Long productId,
            @PathVariable Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getReviewsByProductAndRating(productId, rating, pageable));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        User user = getCurrentUser();
        ReviewResponse response = reviewService.createReview(user, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest request) {

        User user = getCurrentUser();
        return ResponseEntity.ok(reviewService.updateReview(reviewId, user, request));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        User user = getCurrentUser();
        reviewService.deleteReview(reviewId, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> markReviewHelpful(@PathVariable Long reviewId) {
        User user = getCurrentUser();
        reviewService.markReviewHelpful(reviewId, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}/helpful")
    public ResponseEntity<Void> removeHelpfulVote(@PathVariable Long reviewId) {
        User user = getCurrentUser();
        reviewService.removeHelpfulVote(reviewId, user);
        return ResponseEntity.noContent().build();
    }
}