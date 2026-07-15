package com.yourstore.entity;

import com.yourstore.enums.OrderStatus;
import com.yourstore.enums.PaymentMethod;
import com.yourstore.enums.PaymentStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.NEW;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "shipping_phone")
    private String shippingPhone;

    @Column(name = "shipping_cost")
    private BigDecimal shippingCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Column(name = "batch_processed")
    private Boolean batchProcessed = false;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "shipping_date")
    private LocalDateTime shippingDate;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>();

    // ✅ NEW: Payment relationship
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    // Private constructor for Builder


    // Private constructor for Builder
    private Order(Builder builder) {
        this.user = builder.user;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
        this.paymentStatus = builder.paymentStatus;
        this.city = builder.city;
        this.shippingAddress = builder.shippingAddress;
        this.shippingPhone = builder.shippingPhone;
        this.shippingCost = builder.shippingCost;
        this.paymentMethod = builder.paymentMethod;
        this.batchProcessed = builder.batchProcessed;
        this.orderDate = builder.orderDate;
        this.shippingDate = builder.shippingDate;
        this.orderItems = builder.orderItems;
        this.notifications = builder.notifications;
        this.payments = builder.payments;  // ✅ NEW
    }

    public Order() {

    }

    // Builder static inner class
    public static class Builder {
        private User user;
        private BigDecimal totalAmount;
        private OrderStatus status = OrderStatus.NEW;
        private PaymentStatus paymentStatus = PaymentStatus.PENDING;
        private City city;
        private String shippingAddress;
        private String shippingPhone;
        private BigDecimal shippingCost;
        private PaymentMethod paymentMethod;
        private Boolean batchProcessed = false;
        private LocalDateTime orderDate;
        private LocalDateTime shippingDate;
        private List<OrderItem> orderItems = new ArrayList<>();
        private List<Notification> notifications = new ArrayList<>();
        private List<Payment> payments = new ArrayList<>();  // ✅ NEW

        public Builder user(User user) { this.user = user; return this; }
        public Builder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public Builder status(OrderStatus status) { this.status = status; return this; }
        public Builder paymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; return this; }
        public Builder city(City city) { this.city = city; return this; }
        public Builder shippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public Builder shippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; return this; }
        public Builder shippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder batchProcessed(Boolean batchProcessed) { this.batchProcessed = batchProcessed; return this; }
        public Builder orderDate(LocalDateTime orderDate) { this.orderDate = orderDate; return this; }
        public Builder shippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; return this; }
        public Builder orderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; return this; }
        public Builder notifications(List<Notification> notifications) { this.notifications = notifications; return this; }
        public Builder payments(List<Payment> payments) { this.payments = payments; return this; }  // ✅ NEW

        public Order build() {
            return new Order(this);
        }
    }

    // Static factory method
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getShippingPhone() { return shippingPhone; }
    public void setShippingPhone(String shippingPhone) { this.shippingPhone = shippingPhone; }

    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public Boolean getBatchProcessed() { return batchProcessed; }
    public void setBatchProcessed(Boolean batchProcessed) { this.batchProcessed = batchProcessed; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public LocalDateTime getShippingDate() { return shippingDate; }
    public void setShippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public List<Notification> getNotifications() { return notifications; }
    public void setNotifications(List<Notification> notifications) { this.notifications = notifications; }

    // ✅ NEW: Payments getter and setter
    public List<Payment> getPayments() { return payments; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }

    public Integer getTotalItems() {
        return orderItems.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    public boolean canBeShipped() {
        return status == OrderStatus.READY;
    }

    @PrePersist
    protected void onCreate() {
        orderDate = LocalDateTime.now();
    }
}