package com.yourstore.service;

import com.yourstore.dto.request.ProductRequest;
import com.yourstore.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long productId, ProductRequest request);

    void deleteProduct(Long productId);

    ProductResponse getProductById(Long productId);

    Page<ProductResponse> getAllProducts(Pageable pageable);

    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

    Page<ProductResponse> filterProducts(Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<ProductResponse> getBestSellingProducts();

    List<ProductResponse> getNewArrivals();

    List<ProductResponse> getProductsOnSale();

    void updateStock(Long productId, Integer quantity);
}