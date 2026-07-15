package com.yourstore.controller;

import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.Product;
import com.yourstore.mapper.ProductMapper;
import com.yourstore.repository.ProductRepository;
import com.yourstore.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;  // ✅ ADD THIS

    public ProductController(ProductService productService,
                             ProductRepository productRepository,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductResponse>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.searchProducts(keyword, pageable));
    }

    // ✅ FIXED FILTER LOGIC
    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> filterProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        if (minPrice == null) minPrice = BigDecimal.ZERO;
        if (maxPrice == null) maxPrice = BigDecimal.valueOf(999999);

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage;

        if (categoryId != null && brand != null && !brand.isEmpty()) {
            productPage = productRepository.findByCategoryIdAndBrandAndPriceBetween(
                    categoryId, brand, minPrice, maxPrice, pageable);
        } else if (categoryId != null) {
            productPage = productRepository.findByCategoryIdAndPriceBetween(
                    categoryId, minPrice, maxPrice, pageable);
        } else if (brand != null && !brand.isEmpty()) {
            productPage = productRepository.findByBrandAndPriceBetween(
                    brand, minPrice, maxPrice, pageable);
        } else {
            productPage = productRepository.findByPriceBetween(minPrice, maxPrice, pageable);
        }

        return ResponseEntity.ok(productPage.map(productMapper::toResponse));
    }

    @GetMapping("/best-sellers")
    public ResponseEntity<List<ProductResponse>> getBestSellers() {
        return ResponseEntity.ok(productService.getBestSellingProducts());
    }

    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductResponse>> getNewArrivals() {
        return ResponseEntity.ok(productService.getNewArrivals());
    }

    @GetMapping("/on-sale")
    public ResponseEntity<List<ProductResponse>> getProductsOnSale() {
        return ResponseEntity.ok(productService.getProductsOnSale());
    }
}