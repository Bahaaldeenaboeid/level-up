package com.yourstore.pattern.proxy;

import com.yourstore.enums.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary   // 👈 Makes this the default gateway
public class HyperPayServiceImpl implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(HyperPayServiceImpl.class);

    @Override
    public boolean processPayment(Long orderId, PaymentMethod method) {
        logger.info("🔄 Processing payment via HyperPay for order #{} with method: {}", orderId, method);
        // Simulate a real gateway call – replace with actual HTTP client logic
        boolean success = true; // simulate success
        logger.info("Payment result: {}", success ? "SUCCESS" : "FAILED");
        return success;
    }

    @Override
    public boolean refundPayment(Long orderId) {
        logger.info("🔄 Refunding payment via HyperPay for order #{}", orderId);
        return true;
    }
}