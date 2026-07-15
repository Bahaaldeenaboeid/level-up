package com.yourstore.service;

import com.yourstore.dto.request.ReviewRequest;
import com.yourstore.dto.response.ReviewResponse;
import com.yourstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    ReviewResponse createReview(User user, ReviewRequest request);

    ReviewResponse updateReview(Long reviewId, User user, ReviewRequest request);

    void deleteReview(Long reviewId, User user);

    ReviewResponse getReviewById(Long reviewId);

    Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable);

    Page<ReviewResponse> getReviewsByProductAndRating(Long productId, Integer rating, Pageable pageable);

    List<ReviewResponse> getReviewsByUser(User user);

    void markReviewHelpful(Long reviewId, User user);

    void removeHelpfulVote(Long reviewId, User user);

    Double getAverageRating(Long productId);
}