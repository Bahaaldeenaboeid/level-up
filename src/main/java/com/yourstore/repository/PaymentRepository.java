package com.yourstore.repository;

import com.yourstore.entity.Payment;
import com.yourstore.entity.Order;
import com.yourstore.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderOrderByCreatedAtDesc(Order order);

    // ✅ KEEP ONLY ONE VERSION
    List<Payment> findByOrderAndStatus(Order order, PaymentStatus status);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    long countByStatus(PaymentStatus status);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.order.id = :orderId AND p.status = 'PAID'")
    Double getTotalPaidForOrder(Long orderId);

    @Query("SELECT p.paymentMethod, SUM(p.amount) FROM Payment p WHERE p.status = 'PAID' GROUP BY p.paymentMethod")
    List<Object[]> getRevenueByPaymentMethod();

    Page<Payment> findByOrderUserId(Long userId, Pageable pageable);
}