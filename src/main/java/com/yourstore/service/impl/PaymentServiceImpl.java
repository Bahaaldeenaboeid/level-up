package com.yourstore.service.impl;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.entity.Order;
import com.yourstore.entity.Payment;
import com.yourstore.enums.PaymentMethod;
import com.yourstore.enums.PaymentStatus;
import com.yourstore.pattern.proxy.PaymentGateway;   // 👈 renamed interface
import com.yourstore.repository.OrderRepository;
import com.yourstore.repository.PaymentRepository;
import com.yourstore.service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;   // 👈 injected proxy

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository,
                              PaymentGateway paymentGateway) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.paymentGateway = paymentGateway;
    }

    // ===== CRUD =====

    @Override
    public Payment createPayment(Order order, BigDecimal amount, PaymentMethod method) {
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .paymentMethod(method)
                .status(PaymentStatus.PENDING)
                .build();
        return paymentRepository.save(payment);
    }

    @Override
    public Payment createPayment(Order order, Long amount, PaymentMethod method) {
        return createPayment(order, BigDecimal.valueOf(amount), method);
    }

    @Override
    public Payment processPayment(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (isOrderPaid(order.getId())) {
            throw new InvalidRequestException("Order already paid");
        }

        // Create a pending payment record
        Payment payment = createPayment(order, order.getTotalAmount(), method);

        // ✅ Delegate to the gateway (proxy pattern)
        boolean success = paymentGateway.processPayment(order.getId(), method);

        if (success) {
            String transactionId = generateTransactionId();
            payment.markAsPaid(transactionId);
            order.setPaymentStatus(PaymentStatus.PAID);
        } else {
            payment.markAsFailed("Payment gateway declined");
            order.setPaymentStatus(PaymentStatus.FAILED);
        }

        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
    }

    @Override
    public List<Payment> getPaymentsByOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return paymentRepository.findByOrderOrderByCreatedAtDesc(order);
    }

    @Override
    public Page<Payment> getPaymentsByUser(Long userId, Pageable pageable) {
        return paymentRepository.findByOrderUserId(userId, pageable);
    }

    @Override
    public Page<Payment> getAllPayments(Pageable pageable) {
        return paymentRepository.findAll(pageable);
    }

    // ===== STATUS UPDATES =====

    @Override
    public Payment markAsPaid(Long paymentId, String transactionId) {
        Payment payment = getPaymentById(paymentId);
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new InvalidRequestException("Payment already marked as paid");
        }
        payment.markAsPaid(transactionId);
        Order order = payment.getOrder();
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment markAsFailed(Long paymentId, String reason) {
        Payment payment = getPaymentById(paymentId);
        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new InvalidRequestException("Cannot mark a paid payment as failed");
        }
        payment.markAsFailed(reason);
        Order order = payment.getOrder();
        order.setPaymentStatus(PaymentStatus.FAILED);
        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    @Override
    public Payment markAsRefunded(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new InvalidRequestException("Only paid payments can be refunded");
        }
        payment.markAsRefunded();
        Order order = payment.getOrder();
        order.setPaymentStatus(PaymentStatus.REFUNDED);
        orderRepository.save(order);
        return paymentRepository.save(payment);
    }

    // ===== REFUNDS =====

    @Override
    public Payment refundPayment(Long paymentId) {
        return markAsRefunded(paymentId);
    }

    @Override
    public Payment refundPayment(Long orderId, Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        if (!payment.getOrder().getId().equals(orderId)) {
            throw new InvalidRequestException("Payment does not belong to this order");
        }
        return markAsRefunded(paymentId);
    }

    // ===== QUERIES =====

    @Override
    public Payment getLatestSuccessfulPayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        List<Payment> payments = paymentRepository.findByOrderAndStatus(order, PaymentStatus.PAID);
        return payments.isEmpty() ? null : payments.get(0);
    }

    @Override
    public boolean isOrderPaid(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (order.getPaymentStatus() == PaymentStatus.PAID) return true;
        List<Payment> payments = paymentRepository.findByOrderAndStatus(order, PaymentStatus.PAID);
        return !payments.isEmpty();
    }

    @Override
    public Double getTotalPaidForOrder(Long orderId) {
        return paymentRepository.getTotalPaidForOrder(orderId);
    }

    // ===== REPORTS =====

    @Override
    public Double getTotalRevenue() {
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.PAID);
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    @Override
    public List<Object[]> getRevenueByPaymentMethod() {
        return paymentRepository.getRevenueByPaymentMethod();
    }

    @Override
    public long countPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.countByStatus(status);
    }

    // ===== PRIVATE HELPERS =====

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}