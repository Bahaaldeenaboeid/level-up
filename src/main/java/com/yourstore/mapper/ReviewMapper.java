package com.yourstore.mapper;

import com.yourstore.dto.request.ReviewRequest;
import com.yourstore.dto.response.ReviewResponse;
import com.yourstore.entity.Product;
import com.yourstore.entity.Review;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReviewMapper {

    public Review toEntity(ReviewRequest request, User user, Product product) {
        if (request == null || user == null || product == null) {
            return null;
        }
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setImages(request.getImages());
        return review;
    }

    public ReviewResponse toResponse(Review review) {
        if (review == null) {
            return null;
        }
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setProductId(review.getProduct() != null ? review.getProduct().getId() : null);
        response.setProductName(review.getProduct() != null ? review.getProduct().getName() : null);
        response.setUserName(review.getUser() != null ? review.getUser().getName() : null);
        response.setRating(review.getRating());
        response.setTitle(review.getTitle());
        response.setComment(review.getComment());
        response.setImages(review.getImages());
        response.setIsVerifiedPurchase(review.getIsVerifiedPurchase());
        response.setHelpfulCount(review.getHelpfulCount());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        if (reviews == null) {
            return new ArrayList<>();
        }
        return reviews.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}