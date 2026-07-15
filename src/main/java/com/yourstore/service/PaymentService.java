package com.yourstore.service;

import com.yourstore.entity.Payment;
import com.yourstore.entity.Order;
import com.yourstore.enums.PaymentMethod;
import com.yourstore.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    // ===== CRUD =====
    Payment createPayment(Order order, BigDecimal amount, PaymentMethod method);

    Payment createPayment(Order order, Long amount, PaymentMethod method);

    Payment processPayment(Long orderId, PaymentMethod method);

    Payment getPaymentById(Long paymentId);

    List<Payment> getPaymentsByOrder(Long orderId);

    Page<Payment> getPaymentsByUser(Long userId, Pageable pageable);

    Page<Payment> getAllPayments(Pageable pageable);

    // ===== STATUS UPDATES =====
    Payment markAsPaid(Long paymentId, String transactionId);

    Payment markAsFailed(Long paymentId, String reason);

    Payment markAsRefunded(Long paymentId);

    // ===== REFUNDS =====
    Payment refundPayment(Long paymentId);

    Payment refundPayment(Long orderId, Long paymentId);

    // ===== QUERIES =====
    Payment getLatestSuccessfulPayment(Long orderId);

    boolean isOrderPaid(Long orderId);

    Double getTotalPaidForOrder(Long orderId);

    // ===== REPORTS =====
    Double getTotalRevenue();

    List<Object[]> getRevenueByPaymentMethod();

    long countPaymentsByStatus(PaymentStatus status);
}