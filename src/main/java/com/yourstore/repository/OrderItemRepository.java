package com.yourstore.repository;

import com.yourstore.entity.Order;
import com.yourstore.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Find all items for a specific order
    List<OrderItem> findByOrder(Order order);

    // Delete all items for a specific order
    void deleteAllByOrder(Order order);
}