package com.yourstore.service.impl;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.dto.request.BulkCartRequest;
import com.yourstore.dto.request.CartAddRequest;
import com.yourstore.dto.response.CartResponse;
import com.yourstore.entity.CartItem;
import com.yourstore.entity.Product;
import com.yourstore.entity.User;
import com.yourstore.mapper.CartMapper;
import com.yourstore.repository.CartItemRepository;
import com.yourstore.repository.ProductRepository;
import com.yourstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);

    // Default shipping estimate – will be recalculated at order creation
    private static final BigDecimal DEFAULT_SHIPPING_COST = BigDecimal.valueOf(25.00);

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    public CartServiceImpl(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           CartMapper cartMapper) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartMapper = cartMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(User user) {
        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        return cartMapper.toCartResponse(cartItems, DEFAULT_SHIPPING_COST);
    }

    @Override
    @Transactional
    public CartResponse addToCart(User user, CartAddRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));

        if (product.getStock() <= 0) {
            throw new InvalidRequestException("Product '" + product.getName() + "' is out of stock");
        }
        if (request.getQuantity() > product.getStock()) {
            throw new InvalidRequestException("Not enough stock for '" + product.getName() +
                    "'. Available: " + product.getStock());
        }

        CartItem existingItem = cartItemRepository.findByUserAndProductId(user, product.getId()).orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                throw new InvalidRequestException("Cannot add more. You already have " + existingItem.getQuantity() +
                        " in cart. Max available: " + product.getStock());
            }
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
            cartItemRepository.save(cartItem);
        }

        return getCart(user);
    }

    @Override
    @Transactional
    public CartResponse addMultipleToCart(User user, BulkCartRequest request) {
        // Pre‑validation
        for (BulkCartRequest.CartItemEntry item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + item.getProductId()));

            if (product.getStock() <= 0) {
                throw new InvalidRequestException("Product '" + product.getName() + "' is out of stock");
            }
            if (item.getQuantity() > product.getStock()) {
                throw new InvalidRequestException("Not enough stock for '" + product.getName() +
                        "'. Available: " + product.getStock() + ", Requested: " + item.getQuantity());
            }

            CartItem existingItem = cartItemRepository.findByUserAndProductId(user, product.getId()).orElse(null);
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + item.getQuantity();
                if (newQuantity > product.getStock()) {
                    throw new InvalidRequestException("Cannot add more. You already have " + existingItem.getQuantity() +
                            " in cart. Max available: " + product.getStock());
                }
            }
        }

        // Add/update items
        List<CartItem> itemsToSave = new ArrayList<>();
        for (BulkCartRequest.CartItemEntry item : request.getItems()) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + item.getProductId()));

            CartItem existingItem = cartItemRepository.findByUserAndProductId(user, product.getId()).orElse(null);

            if (existingItem != null) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                itemsToSave.add(existingItem);
            } else {
                CartItem cartItem = new CartItem();
                cartItem.setUser(user);
                cartItem.setProduct(product);
                cartItem.setQuantity(item.getQuantity());
                itemsToSave.add(cartItem);
            }
        }
        cartItemRepository.saveAll(itemsToSave);

        // Return updated cart – use the default shipping cost, no city lookup
        List<CartItem> updatedCartItems = cartItemRepository.findByUser(user);
        return cartMapper.toCartResponse(updatedCartItems, DEFAULT_SHIPPING_COST);
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(User user, Long productId, Integer quantity) {
        if (quantity < 0) {
            throw new InvalidRequestException("Quantity cannot be negative");
        }

        CartItem cartItem = cartItemRepository.findByUserAndProductId(user, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        Product product = cartItem.getProduct();

        if (quantity > product.getStock()) {
            throw new InvalidRequestException("Not enough stock. Available: " + product.getStock());
        }

        if (quantity == 0) {
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
        }

        return getCart(user);
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(User user, Long productId) {
        cartItemRepository.deleteByUserAndProductId(user, productId);
        return getCart(user);
    }

    @Override
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteAllByUser(user);
    }

    @Override
    public List<CartItem> getCartItems(User user) {
        return cartItemRepository.findByUser(user);
    }
}