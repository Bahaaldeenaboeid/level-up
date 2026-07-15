package com.yourstore.service.impl;

import com.yourstore.dto.request.WishlistRequest;
import com.yourstore.dto.response.WishlistResponse;
import com.yourstore.entity.Product;
import com.yourstore.entity.User;
import com.yourstore.entity.Wishlist;
import com.yourstore.core.exception.DuplicateResourceException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.mapper.WishlistMapper;
import com.yourstore.repository.ProductRepository;
import com.yourstore.repository.WishlistRepository;
import com.yourstore.service.WishlistService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final WishlistMapper wishlistMapper;

    public WishlistServiceImpl(WishlistRepository wishlistRepository,
                               ProductRepository productRepository,
                               WishlistMapper wishlistMapper) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.wishlistMapper = wishlistMapper;
    }

    @Override
    public WishlistResponse addToWishlist(User user, WishlistRequest request) {
        if (wishlistRepository.existsByUserAndProductId(user, request.getProductId())) {
            throw new DuplicateResourceException("Product already in wishlist");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Wishlist wishlist = wishlistMapper.toEntity(request, user, product);
        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return wishlistMapper.toResponse(savedWishlist);
    }

    @Override
    public void removeFromWishlist(User user, Long productId) {
        wishlistRepository.deleteByUserAndProductId(user, productId);
    }

    @Override
    public Page<WishlistResponse> getWishlist(User user, Pageable pageable) {
        return wishlistRepository.findByUserOrderByAddedAtDesc(user, pageable)
                .map(wishlistMapper::toResponse);
    }

    @Override
    public boolean isProductInWishlist(User user, Long productId) {
        return wishlistRepository.existsByUserAndProductId(user, productId);
    }
}