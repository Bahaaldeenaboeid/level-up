package com.yourstore.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AdminProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String brand;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private String discountType;

    private BigDecimal discountValue;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private Map<String, Object> specs;

    private Long categoryId;

    private MultipartFile mainImage;

    private List<MultipartFile> thumbnails;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Map<String, Object> getSpecs() { return specs; }
    public void setSpecs(Map<String, Object> specs) { this.specs = specs; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public MultipartFile getMainImage() { return mainImage; }
    public void setMainImage(MultipartFile mainImage) { this.mainImage = mainImage; }

    public List<MultipartFile> getThumbnails() { return thumbnails; }
    public void setThumbnails(List<MultipartFile> thumbnails) { this.thumbnails = thumbnails; }
}