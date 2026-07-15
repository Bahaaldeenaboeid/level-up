package com.yourstore.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BulkCartRequest {

    @Valid
    @NotNull(message = "Items list cannot be null")
    private List<CartItemEntry> items;

    public List<CartItemEntry> getItems() { return items; }
    public void setItems(List<CartItemEntry> items) { this.items = items; }

    public static class CartItemEntry {

        @NotNull(message = "Product ID is required")
        private Long productId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity = 1;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}