package com.yourstore.repository;

import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import com.yourstore.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders for a specific user
    List<Order> findByUserOrderByOrderDateDesc(User user);

    // Find orders by status (with pagination) ✅ FIXED: Added Pageable
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    // Find orders by status and batch_processed = false (used exclusively by the batch job)
    List<Order> findByStatusAndBatchProcessedFalse(OrderStatus status);

    // Find orders by user and status
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    // Find orders between dates (for reports)
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find orders by city (for reports)
    @Query("SELECT o FROM Order o WHERE o.city.id = :cityId")
    List<Order> findByCityId(@Param("cityId") Long cityId);

    // Get total sales for a specific date range
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.paymentStatus = 'PAID'")
    Double getTotalSalesBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Get order count for a specific date range
    @Query("SELECT COUNT(o) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate AND o.paymentStatus = 'PAID'")
    Long getOrderCountBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find orders that are ready for shipping (status = READY and batch_processed = false)
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.batchProcessed = false")
    List<Order> findReadyOrdersForBatch(@Param("status") OrderStatus status);
}