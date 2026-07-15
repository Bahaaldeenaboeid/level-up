package com.yourstore.pattern.proxy;

import com.yourstore.enums.PaymentMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayTabsServiceImpl implements PaymentGateway {

    private static final Logger logger = LoggerFactory.getLogger(PayTabsServiceImpl.class);

    @Override
    public boolean processPayment(Long orderId, PaymentMethod method) {
        logger.info("🔄 Processing payment via PayTabs for order #{} with method: {}", orderId, method);
        boolean success = true;
        logger.info("Payment result: {}", success ? "SUCCESS" : "FAILED");
        return success;
    }

    @Override
    public boolean refundPayment(Long orderId) {
        logger.info("🔄 Refunding payment via PayTabs for order #{}", orderId);
        return true;
    }
}