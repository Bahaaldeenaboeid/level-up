package com.yourstore.pattern.facade;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.response.CartResponse;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.Payment;
import com.yourstore.entity.User;
import com.yourstore.service.CartService;
import com.yourstore.service.EmailService;
import com.yourstore.service.OrderService;
import com.yourstore.service.PaymentService;   // 👈 business service (not the proxy)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CheckoutFacade {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutFacade.class);

    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;   // 👈 injected business PaymentService
    private final EmailService emailService;

    public CheckoutFacade(CartService cartService,
                          OrderService orderService,
                          PaymentService paymentService,
                          EmailService emailService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.emailService = emailService;
    }

    public OrderResponse checkout(User user, OrderRequest request) {
        if (user == null) {
            throw new InvalidRequestException("user", "User must not be null");
        }
        if (request == null) {
            throw new InvalidRequestException("orderRequest", "Order request must not be null");
        }

        CartResponse cart = cartService.getCart(user);
        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidRequestException("cart", "Cart cannot be empty");
        }

        logger.info("Starting checkout for user {}", user.getId());

        // 1. Create order
        OrderResponse orderResponse = orderService.createOrder(user, request);
        if (orderResponse == null) {
            throw new InvalidRequestException("order", "Could not create order");
        }

        // 2. Process payment — returns Payment object
        Payment payment = paymentService.processPayment(orderResponse.getId(), request.getPaymentMethod());

        // 3. Check if payment was successful
        if (!payment.isSuccessful()) {
            emailService.sendPaymentFailedEmail(orderResponse.getId());
            throw new RuntimeException("Payment failed for order #" + orderResponse.getId() + ": " + payment.getFailureReason());
        }

        // 4. Clear cart
        cartService.clearCart(user);

        // 5. Send confirmation email
        emailService.sendOrderConfirmation(orderResponse.getId());

        logger.info("Completed checkout for user {} and order {}", user.getId(), orderResponse.getId());
        return orderResponse;
    }
}