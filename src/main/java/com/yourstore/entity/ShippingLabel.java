package com.yourstore.entity;

import java.time.LocalDateTime;

public class ShippingLabel {

    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private Long orderId;
    private String city;
    private LocalDateTime shippingDate;

    // Private constructor for Builder
    private ShippingLabel() {}

    // Private constructor for Builder
    private ShippingLabel(Builder builder) {
        this.customerName = builder.customerName;
        this.customerAddress = builder.customerAddress;
        this.customerPhone = builder.customerPhone;
        this.orderId = builder.orderId;
        this.city = builder.city;
        this.shippingDate = builder.shippingDate;
    }

    // Builder static inner class
    public static class Builder {
        private String customerName;
        private String customerAddress;
        private String customerPhone;
        private Long orderId;
        private String city;
        private LocalDateTime shippingDate;

        public Builder customerName(String customerName) { this.customerName = customerName; return this; }
        public Builder customerAddress(String customerAddress) { this.customerAddress = customerAddress; return this; }
        public Builder customerPhone(String customerPhone) { this.customerPhone = customerPhone; return this; }
        public Builder orderId(Long orderId) { this.orderId = orderId; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder shippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; return this; }

        public ShippingLabel build() {
            return new ShippingLabel(this);
        }
    }

    // Static factory method
    public static Builder builder() {
        return new Builder();
    }

    // Getters and Setters
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDateTime getShippingDate() { return shippingDate; }
    public void setShippingDate(LocalDateTime shippingDate) { this.shippingDate = shippingDate; }

    @Override
    public String toString() {
        return "ShippingLabel{" +
                "customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerPhone='" + customerPhone + '\'' +
                ", orderId=" + orderId +
                ", city='" + city + '\'' +
                ", shippingDate=" + shippingDate +
                '}';
    }
}