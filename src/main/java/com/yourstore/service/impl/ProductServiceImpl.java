package com.yourstore.service.impl;

import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.dto.request.ProductRequest;
import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.Category;
import com.yourstore.entity.Product;
import com.yourstore.mapper.ProductMapper;
import com.yourstore.repository.CategoryRepository;
import com.yourstore.repository.ProductRepository;
import com.yourstore.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        Product product = productMapper.toEntity(request, category);
        // ✅ Product entity calculates finalPrice automatically via @PrePersist
        Product savedProduct = productRepository.save(product);

        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        Product product = getProductEntity(productId);
        Category category = null;

        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        }

        productMapper.updateEntity(request, product, category);
        // ✅ Product entity calculates finalPrice automatically via @PreUpdate
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = getProductEntity(productId);
        productRepository.delete(product);
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        Product product = getProductEntity(productId);
        return productMapper.toResponse(product);
    }

    @Override
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword, keyword, pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public Page<ProductResponse> filterProducts(Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (categoryId != null && brand != null) {
            return productRepository.findByCategoryIdAndPriceBetween(categoryId, minPrice, maxPrice, pageable)
                    .map(productMapper::toResponse);
        } else if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable)
                    .map(productMapper::toResponse);
        } else if (brand != null) {
            return productRepository.findByBrandIgnoreCase(brand, pageable)
                    .map(productMapper::toResponse);
        } else {
            return productRepository.findByPriceBetween(minPrice, maxPrice, pageable)
                    .map(productMapper::toResponse);
        }
    }

    @Override
    public List<ProductResponse> getBestSellingProducts() {
        return productRepository.findBestSellingProducts(org.springframework.data.domain.PageRequest.of(0, 10))
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getNewArrivals() {
        return productRepository.findTop12ByOrderByCreatedAtDesc()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsOnSale() {
        return productRepository.findProductsWithActiveDiscounts()
                .stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = getProductEntity(productId);
        product.setStock(quantity);
        productRepository.save(product);
    }

    private Product getProductEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
    }
}