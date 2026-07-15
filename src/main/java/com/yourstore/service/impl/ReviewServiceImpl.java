package com.yourstore.service.impl;

import com.yourstore.dto.request.ReviewRequest;
import com.yourstore.dto.response.ReviewResponse;
import com.yourstore.entity.Product;
import com.yourstore.entity.Review;
import com.yourstore.entity.ReviewHelpfulVote;
import com.yourstore.entity.User;
import com.yourstore.core.exception.DuplicateResourceException;
import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.mapper.ReviewMapper;
import com.yourstore.repository.ProductRepository;
import com.yourstore.repository.ReviewHelpfulVoteRepository;
import com.yourstore.repository.ReviewRepository;
import com.yourstore.service.ReviewService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewHelpfulVoteRepository helpfulVoteRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ReviewHelpfulVoteRepository helpfulVoteRepository,
                             ProductRepository productRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.helpfulVoteRepository = helpfulVoteRepository;
        this.productRepository = productRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ReviewResponse createReview(User user, ReviewRequest request) {
        if (reviewRepository.existsByUserIdAndProductId(user.getId(), request.getProductId())) {
            throw new DuplicateResourceException("You have already reviewed this product");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Review review = reviewMapper.toEntity(request, user, product);

        boolean verified = user.getOrders().stream()
                .flatMap(order -> order.getOrderItems().stream())
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        review.setIsVerifiedPurchase(verified);

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(savedReview);
    }

    @Override
    public ReviewResponse updateReview(Long reviewId, User user, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new InvalidRequestException("You do not have permission to update this review");
        }

        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());

        if (request.getImages() != null) {
            review.setImages(request.getImages());
        }

        Review updatedReview = reviewRepository.save(review);
        return reviewMapper.toResponse(updatedReview);
    }

    @Override
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new InvalidRequestException("You do not have permission to delete this review");
        }

        reviewRepository.delete(review);
    }

    @Override
    public ReviewResponse getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        return reviewMapper.toResponse(review);
    }

    @Override
    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public Page<ReviewResponse> getReviewsByProductAndRating(Long productId, Integer rating, Pageable pageable) {
        return reviewRepository.findByProductIdAndRating(productId, rating, pageable)
                .map(reviewMapper::toResponse);
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(User user) {
        List<Review> reviews = reviewRepository.findByUserOrderByCreatedAtDesc(user);
        return reviewMapper.toResponseList(reviews);
    }

    @Override
    public void markReviewHelpful(Long reviewId, User user) {
        if (helpfulVoteRepository.existsByReviewIdAndUserId(reviewId, user.getId())) {
            throw new DuplicateResourceException("You have already marked this review as helpful");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        ReviewHelpfulVote vote = new ReviewHelpfulVote();
        vote.setReview(review);
        vote.setUser(user);

        helpfulVoteRepository.save(vote);

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    @Override
    public void removeHelpfulVote(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        ReviewHelpfulVote vote = helpfulVoteRepository.findByReviewAndUser(review, user)
                .orElseThrow(() -> new ResourceNotFoundException("Helpful vote not found"));

        helpfulVoteRepository.delete(vote);

        review.setHelpfulCount(review.getHelpfulCount() - 1);
        reviewRepository.save(review);
    }

    @Override
    public Double getAverageRating(Long productId) {
        return reviewRepository.getAverageRatingForProduct(productId);
    }
}