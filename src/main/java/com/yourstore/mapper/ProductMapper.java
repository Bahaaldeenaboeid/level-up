package com.yourstore.mapper;

import com.yourstore.dto.request.ProductRequest;
import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.Category;
import com.yourstore.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductRequest request, Category category) {
        if (request == null) {
            return null;
        }
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setDiscountValue(request.getDiscountValue());
        product.setStock(request.getStock());
        product.setSpecs(request.getSpecs());
        product.setCategory(category);
        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setDiscountValue(product.getDiscountValue());
        response.setFinalPrice(product.getFinalPrice());
        response.setStock(product.getStock());
        response.setSpecs(product.getSpecs());
        response.setMainImage(product.getMainImage());
        response.setThumbnails(product.getThumbnails());
        response.setCreatedAt(product.getCreatedAt());

        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }

        if (product.getReviews() != null && !product.getReviews().isEmpty()) {
            double avg = product.getReviews().stream()
                    .mapToInt(r -> r.getRating())
                    .average()
                    .orElse(0.0);
            response.setAverageRating(Math.round(avg * 10.0) / 10.0);
            response.setReviewCount(product.getReviews().size());
        } else {
            response.setAverageRating(0.0);
            response.setReviewCount(0);
        }

        return response;
    }

    public void updateEntity(ProductRequest request, Product product, Category category) {
        if (request == null || product == null) {
            return;
        }
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getDiscountValue() != null) {
            product.setDiscountValue(request.getDiscountValue());
        }
        if (request.getStock() != null) {
            product.setStock(request.getStock());
        }
        if (request.getSpecs() != null) {
            product.setSpecs(request.getSpecs());
        }
        if (category != null) {
            product.setCategory(category);
        }
    }
}