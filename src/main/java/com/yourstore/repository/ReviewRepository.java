package com.yourstore.repository;

import com.yourstore.entity.Review;
import com.yourstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Find all reviews for a product (with pagination)
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    // Find reviews by rating for a specific product
    Page<Review> findByProductIdAndRating(Long productId, Integer rating, Pageable pageable);

    // Find reviews by user
    List<Review> findByUserOrderByCreatedAtDesc(User user);

    // Check if user has already reviewed this product
    Optional<Review> findByUserIdAndProductId(Long userId, Long productId);

    // Check if user has reviewed this product
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Get average rating for a product
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingForProduct(@Param("productId") Long productId);

    // Get rating breakdown for a product
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingBreakdownForProduct(@Param("productId") Long productId);
}