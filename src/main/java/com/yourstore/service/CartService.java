package com.yourstore.service;

import com.yourstore.dto.request.BulkCartRequest;
import com.yourstore.dto.request.CartAddRequest;
import com.yourstore.dto.response.CartResponse;
import com.yourstore.entity.CartItem;
import com.yourstore.entity.User;

import java.util.List;

public interface CartService {

    CartResponse getCart(User user);

    CartResponse addToCart(User user, CartAddRequest request);

    CartResponse addMultipleToCart(User user, BulkCartRequest request);

    CartResponse updateQuantity(User user, Long productId, Integer quantity);

    CartResponse removeFromCart(User user, Long productId);

    void clearCart(User user);

    List<CartItem> getCartItems(User user);
}