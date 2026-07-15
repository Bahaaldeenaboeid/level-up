package com.yourstore.service.impl;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.dto.request.OrderRequest;
import com.yourstore.dto.request.OrderStatusUpdateRequest;
import com.yourstore.dto.response.OrderResponse;
import com.yourstore.entity.*;
import com.yourstore.enums.NotificationType;
import com.yourstore.enums.OrderStatus;
import com.yourstore.enums.PaymentStatus;
import com.yourstore.mapper.OrderMapper;
import com.yourstore.pattern.observer.OrderStatusChangedEvent;
import com.yourstore.pattern.state.OrderStateMachine;
import com.yourstore.repository.OrderRepository;
import com.yourstore.repository.ProductRepository;
import com.yourstore.service.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final CityService cityService;
    private final CartService cartService;
    private final PaymentService paymentService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final OrderStateMachine orderStateMachine;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper,
                            CityService cityService,
                            CartService cartService,
                            PaymentService paymentService,
                            EmailService emailService,
                            NotificationService notificationService,
                            ApplicationEventPublisher eventPublisher,
                            OrderStateMachine orderStateMachine) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
        this.cityService = cityService;
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.eventPublisher = eventPublisher;
        this.orderStateMachine = orderStateMachine;
    }

    @Override
    public OrderResponse createOrder(User user, OrderRequest request) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        if (cartItems.isEmpty()) {
            throw new InvalidRequestException("Cart is empty");
        }

        City city = cityService.getCityById(request.getCityId());

        BigDecimal subtotal = calculateSubtotal(cartItems);
        BigDecimal shippingCost = BigDecimal.valueOf(city.getShippingRate());
        BigDecimal totalAmount = subtotal.add(shippingCost);

        Order order = orderMapper.toEntity(request, user, city, totalAmount, shippingCost);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new InvalidRequestException("Insufficient stock for: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtTime(product.getFinalPrice());

            orderItems.add(orderItem);

            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(user);

        // ✅ Observer — publish event
        eventPublisher.publishEvent(new OrderStatusChangedEvent(savedOrder, null, OrderStatus.NEW));

        // ✅ Observer — send notifications and emails
        notificationService.createNotification(
                user,
                savedOrder,
                NotificationType.ORDER_PLACED,
                "Order Placed",
                "Your order #" + savedOrder.getId() + " has been placed successfully."
        );

        // ✅ Observer — sends order confirmation email
        emailService.sendOrderConfirmation(savedOrder.getId());

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long orderId, User user) {
        Order order = findOrderById(orderId);
        if (!order.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw new InvalidRequestException("You do not have permission to view this order");
        }
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getUserOrders(User user) {
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        return orderMapper.toResponseList(orders);
    }

    @Override
    public Page<OrderResponse> getOrdersByStatus(OrderStatus status, Pageable pageable) {
        // ✅ FIXED: Now uses pageable correctly
        return orderRepository.findByStatus(status, pageable)
                .map(orderMapper::toResponse);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatusUpdateRequest request) {
        Order order = findOrderById(orderId);
        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // ✅ State pattern — validate transition
        orderStateMachine.transition(order, newStatus);

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // ✅ Observer — publish event
        eventPublisher.publishEvent(new OrderStatusChangedEvent(updatedOrder, oldStatus, newStatus));

        // ✅ Observer — send notifications
        sendStatusNotifications(updatedOrder, newStatus);

        return orderMapper.toResponse(updatedOrder);
    }

    @Override
    public void confirmPayment(Long orderId) {
        Order order = findOrderById(orderId);
        order.setPaymentStatus(PaymentStatus.PAID);
        orderRepository.save(order);
    }

    @Override
    public Order findOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
    }

    @Override
    public List<Order> getReadyOrdersForBatch() {
        return orderRepository.findByStatusAndBatchProcessedFalse(OrderStatus.READY);
    }

    @Override
    public void markOrdersAsShipped(List<Order> orders) {
        for (Order order : orders) {
            // ✅ State pattern — validate transition to SHIPPED
            orderStateMachine.transition(order, OrderStatus.SHIPPED);
            order.setStatus(OrderStatus.SHIPPED);
            order.setBatchProcessed(true);
            order.setShippingDate(LocalDateTime.now());
            orderRepository.save(order);

            // ✅ Observer — publish event
            eventPublisher.publishEvent(new OrderStatusChangedEvent(order, OrderStatus.READY, OrderStatus.SHIPPED));

            // ✅ Observer — send delivery email
            emailService.sendOrderShippedEmail(order.getId());
        }
    }

    @Override
    public OrderResponse getOrderForTracking(Long orderId, User user) {
        Order order = findOrderById(orderId);
        return orderMapper.toResponse(order);
    }

    private BigDecimal calculateSubtotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void sendStatusNotifications(Order order, OrderStatus status) {
        User user = order.getUser();

        switch (status) {
            case PROCESSING:
                notificationService.createNotification(
                        user, order, NotificationType.PROCESSING,
                        "Order Processing",
                        "Your order #" + order.getId() + " is being processed."
                );
                emailService.sendOrderProcessingEmail(order.getId());
                break;
            case READY:
                notificationService.createNotification(
                        user, order, NotificationType.READY,
                        "Order Ready",
                        "Your order #" + order.getId() + " is ready for shipping."
                );
                emailService.sendOrderReadyEmail(order.getId());
                break;
            case SHIPPED:
                notificationService.createNotification(
                        user, order, NotificationType.SHIPPED,
                        "Order Shipped",
                        "Your order #" + order.getId() + " has been shipped and is being delivered today."
                );
                emailService.sendOrderShippedEmail(order.getId());
                break;
            default:
                break;
        }
    }
}