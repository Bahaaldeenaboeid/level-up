package com.yourstore.service;

import com.yourstore.dto.request.WishlistRequest;
import com.yourstore.dto.response.WishlistResponse;
import com.yourstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishlistService {

    WishlistResponse addToWishlist(User user, WishlistRequest request);

    void removeFromWishlist(User user, Long productId);

    Page<WishlistResponse> getWishlist(User user, Pageable pageable);

    boolean isProductInWishlist(User user, Long productId);
}