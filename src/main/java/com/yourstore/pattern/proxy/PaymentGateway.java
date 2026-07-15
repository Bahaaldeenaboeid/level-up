package com.yourstore.pattern.proxy;

import com.yourstore.enums.PaymentMethod;

public interface PaymentGateway {
    boolean processPayment(Long orderId, PaymentMethod method);
    boolean refundPayment(Long orderId);
}