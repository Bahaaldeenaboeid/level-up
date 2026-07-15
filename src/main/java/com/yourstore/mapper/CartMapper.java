package com.yourstore.mapper;

import com.yourstore.dto.request.CartAddRequest;
import com.yourstore.dto.response.CartResponse;
import com.yourstore.entity.CartItem;
import com.yourstore.entity.Product;
import com.yourstore.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartMapper {

    public CartItem toEntity(CartAddRequest request, User user, Product product) {
        if (request == null || user == null || product == null) {
            return null;
        }
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        return cartItem;
    }

    public CartResponse.CartItemResponse toCartItemResponse(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        CartResponse.CartItemResponse response = new CartResponse.CartItemResponse();
        response.setProductId(cartItem.getProduct().getId());
        response.setProductName(cartItem.getProduct().getName());
        response.setProductImage(cartItem.getProduct().getMainImage());
        response.setUnitPrice(cartItem.getProduct().getFinalPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setSubtotal(cartItem.getSubtotal());
        return response;
    }

    public List<CartResponse.CartItemResponse> toCartItemResponseList(List<CartItem> cartItems) {
        if (cartItems == null) {
            return new ArrayList<>();
        }
        return cartItems.stream()
                .map(this::toCartItemResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    public CartResponse toCartResponse(List<CartItem> cartItems, BigDecimal shippingCost) {
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        List<CartResponse.CartItemResponse> itemResponses = toCartItemResponseList(cartItems);
        return new CartResponse(itemResponses, shippingCost);
    }
}