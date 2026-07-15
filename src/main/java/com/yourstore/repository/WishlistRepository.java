package com.yourstore.repository;

import com.yourstore.entity.Wishlist;
import com.yourstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Find all wishlist items for a user
    Page<Wishlist> findByUserOrderByAddedAtDesc(User user, Pageable pageable);

    // Find a specific wishlist item by user and product
    Optional<Wishlist> findByUserAndProductId(User user, Long productId);

    // Check if a product is in a user's wishlist
    boolean existsByUserAndProductId(User user, Long productId);

    // Delete a specific wishlist item by user and product
    void deleteByUserAndProductId(User user, Long productId);

    // Delete all wishlist items for a user (used when account is deleted)
    void deleteAllByUser(User user);
}