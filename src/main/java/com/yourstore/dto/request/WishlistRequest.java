package com.yourstore.dto.request;

import jakarta.validation.constraints.NotNull;

public class WishlistRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
}