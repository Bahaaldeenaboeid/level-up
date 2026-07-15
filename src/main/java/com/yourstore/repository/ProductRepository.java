package com.yourstore.repository;

import com.yourstore.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String nameKeyword, String brandKeyword, String descriptionKeyword, Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByBrandIgnoreCase(String brand, Pageable pageable);

    Page<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategoryIdAndPriceBetween(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByCategoryIdAndBrandAndPriceBetween(
            Long categoryId, String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Product> findByBrandAndPriceBetween(
            String brand, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    List<Product> findByStockLessThan(Integer threshold);

    // ✅ FIXED: Use discountValue instead of discountType
    @Query("SELECT p FROM Product p WHERE p.discountValue IS NOT NULL AND p.discountValue > 0")
    List<Product> findProductsWithActiveDiscounts();

    @Query("SELECT p FROM Product p JOIN OrderItem oi ON p.id = oi.product.id GROUP BY p.id ORDER BY SUM(oi.quantity) DESC")
    List<Product> findBestSellingProducts(Pageable pageable);

    List<Product> findTop12ByOrderByCreatedAtDesc();
}