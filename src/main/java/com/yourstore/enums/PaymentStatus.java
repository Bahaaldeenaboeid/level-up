package com.yourstore.enums;

public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED,      // ← ADD THIS
    PARTIALLY_REFUNDED  // ← ADD THIS (optional)
}