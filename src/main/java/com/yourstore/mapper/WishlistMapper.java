package com.yourstore.mapper;

import com.yourstore.dto.request.WishlistRequest;
import com.yourstore.dto.response.WishlistResponse;
import com.yourstore.entity.Product;
import com.yourstore.entity.User;
import com.yourstore.entity.Wishlist;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WishlistMapper {

    public Wishlist toEntity(WishlistRequest request, User user, Product product) {
        if (request == null || user == null || product == null) {
            return null;
        }
        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setProduct(product);
        return wishlist;
    }

    public WishlistResponse toResponse(Wishlist wishlist) {
        if (wishlist == null) {
            return null;
        }
        WishlistResponse response = new WishlistResponse();
        response.setId(wishlist.getId());
        response.setProductId(wishlist.getProduct() != null ? wishlist.getProduct().getId() : null);
        response.setProductName(wishlist.getProductName());
        response.setProductImage(wishlist.getProduct() != null ? wishlist.getProduct().getMainImage() : null);
        response.setProductPrice(wishlist.getProduct() != null ? wishlist.getProduct().getPrice() : null);
        response.setProductFinalPrice(wishlist.getProductPrice());
        response.setAddedAt(wishlist.getAddedAt());
        return response;
    }

    public List<WishlistResponse> toResponseList(List<Wishlist> wishlistItems) {
        if (wishlistItems == null) {
            return new ArrayList<>();
        }
        return wishlistItems.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}