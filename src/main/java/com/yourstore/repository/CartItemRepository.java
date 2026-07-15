package com.yourstore.repository;

import com.yourstore.entity.CartItem;
import com.yourstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Find all cart items for a specific user
    List<CartItem> findByUser(User user);

    // Find a specific cart item for a user and product
    Optional<CartItem> findByUserAndProductId(User user, Long productId);

    // Delete all cart items for a user (used after order completion)
    void deleteAllByUser(User user);

    // Delete a specific cart item for a user
    void deleteByUserAndProductId(User user, Long productId);

    // Count items in cart for a user
    long countByUser(User user);
}