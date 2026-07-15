package com.yourstore.controller;

import com.yourstore.dto.request.RefundRequest;
import com.yourstore.dto.response.PaymentResponse;
import com.yourstore.entity.Payment;
import com.yourstore.mapper.PaymentMapper;
import com.yourstore.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService,
                             PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    // ===== CUSTOMER ENDPOINTS =====

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(@PathVariable Long orderId) {
        List<Payment> payments = paymentService.getPaymentsByOrder(orderId);
        return ResponseEntity.ok(
                payments.stream()
                        .map(paymentMapper::toResponse)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/order/{orderId}/latest")
    public ResponseEntity<PaymentResponse> getLatestPayment(@PathVariable Long orderId) {
        Payment payment = paymentService.getLatestSuccessfulPayment(orderId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @GetMapping("/order/{orderId}/paid")
    public ResponseEntity<Boolean> isOrderPaid(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.isOrderPaid(orderId));
    }

    // ===== ADMIN ENDPOINTS =====

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(
                paymentService.getAllPayments(pageable)
                        .map(paymentMapper::toResponse)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        Payment payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentResponse> refundPayment(
            @PathVariable Long id,
            @Valid @RequestBody RefundRequest request) {

        Payment payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/revenue")
    public ResponseEntity<Double> getTotalRevenue() {
        return ResponseEntity.ok(paymentService.getTotalRevenue());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reports/by-method")
    public ResponseEntity<List<PaymentMethodRevenue>> getRevenueByMethod() {
        List<Object[]> results = paymentService.getRevenueByPaymentMethod();
        List<PaymentMethodRevenue> response = results.stream()
                .map(row -> new PaymentMethodRevenue(
                        row[0].toString(),
                        (Double) row[1]
                ))
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}

// Inner class for revenue by payment method
class PaymentMethodRevenue {
    private String paymentMethod;
    private Double revenue;

    public PaymentMethodRevenue(String paymentMethod, Double revenue) {
        this.paymentMethod = paymentMethod;
        this.revenue = revenue;
    }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Double getRevenue() { return revenue; }
    public void setRevenue(Double revenue) { this.revenue = revenue; }
}