package com.yourstore.controller;

import com.yourstore.dto.request.OrderStatusUpdateRequest;
import com.yourstore.dto.request.ProductRequest;
import com.yourstore.dto.response.AdminCustomerResponse;
import com.yourstore.dto.response.AdminOrderResponse;
import com.yourstore.dto.response.AdminReportResponse;
import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.Category;
import com.yourstore.entity.City;
import com.yourstore.service.*;
import com.yourstore.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final CityService cityService;
    private final ProductImageService productImageService;

    public AdminController(AdminService adminService,
                           ProductService productService,
                           OrderService orderService,
                           CategoryService categoryService,
                           CityService cityService,
                           ProductImageService productImageService) {
        this.adminService = adminService;
        this.productService = productService;
        this.orderService = orderService;
        this.categoryService = categoryService;
        this.cityService = cityService;
        this.productImageService = productImageService;
    }

    // ===== PRODUCT MANAGEMENT =====

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> addProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ===== DISCOUNT MANAGEMENT =====

    @PutMapping("/products/{id}/discount")
    public ResponseEntity<ProductResponse> updateDiscount(
            @PathVariable Long id,
            @RequestParam Double discountValue) {

        ProductRequest request = new ProductRequest();
        request.setDiscountValue(BigDecimal.valueOf(discountValue));
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/products/{id}/discount")
    public ResponseEntity<ProductResponse> removeDiscount(@PathVariable Long id) {
        ProductRequest request = new ProductRequest();
        request.setDiscountValue(BigDecimal.ZERO);
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    // ===== IMAGE MANAGEMENT =====

    @PutMapping("/products/{id}/images/main")
    public ResponseEntity<ProductResponse> updateMainImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image) {
        productImageService.updateMainImage(id, image);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/products/{id}/images/thumbnails/{index}")
    public ResponseEntity<ProductResponse> updateThumbnail(
            @PathVariable Long id,
            @PathVariable int index,
            @RequestParam("image") MultipartFile image) {
        productImageService.updateThumbnail(id, index, image);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/products/{id}/images/main")
    public ResponseEntity<ProductResponse> deleteMainImage(@PathVariable Long id) {
        productImageService.deleteMainImage(id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/products/{id}/images/thumbnails/{index}")
    public ResponseEntity<ProductResponse> deleteThumbnail(
            @PathVariable Long id,
            @PathVariable int index) {
        productImageService.deleteThumbnail(id, index);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/products/{id}/images/thumbnails")
    public ResponseEntity<ProductResponse> deleteAllThumbnails(@PathVariable Long id) {
        productImageService.deleteAllThumbnails(id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // ===== ORDER MANAGEMENT =====

    @GetMapping("/orders")
    public ResponseEntity<Page<AdminOrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getAllOrders(pageable));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<AdminOrderResponse> getOrderDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getOrderDetails(id));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<AdminOrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        OrderStatusUpdateRequest request = new OrderStatusUpdateRequest();
        request.setStatus(com.yourstore.enums.OrderStatus.valueOf(status.toUpperCase()));
        orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(adminService.getOrderDetails(id));
    }

    @PutMapping("/orders/{id}/payment")
    public ResponseEntity<AdminOrderResponse> confirmPayment(@PathVariable Long id) {
        orderService.confirmPayment(id);
        return ResponseEntity.ok(adminService.getOrderDetails(id));
    }

    // ===== CATEGORY MANAGEMENT =====

    @PostMapping("/categories")
    public ResponseEntity<Category> addCategory(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return new ResponseEntity<>(
                categoryService.createCategory(name, description),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(categoryService.updateCategory(id, name, description));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ===== CITY / SHIPPING MANAGEMENT =====

    @PostMapping("/cities")
    public ResponseEntity<City> addCity(
            @RequestParam String name,
            @RequestParam Double shippingRate) {
        return new ResponseEntity<>(cityService.createCity(name, shippingRate), HttpStatus.CREATED);
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<City> updateCity(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double shippingRate) {
        return ResponseEntity.ok(cityService.updateCity(id, name, shippingRate));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }

    // ===== REPORTS =====

    @GetMapping("/reports/daily-sales")
    public ResponseEntity<AdminReportResponse> getDailySalesReport(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().toString()}") String date) {
        return ResponseEntity.ok(adminService.getDailySalesReport(LocalDate.parse(date)));
    }

    @GetMapping("/reports/monthly-sales")
    public ResponseEntity<AdminReportResponse> getMonthlySalesReport(
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(adminService.getMonthlySalesReport(year, month));
    }

    @GetMapping("/reports/yearly-sales")
    public ResponseEntity<AdminReportResponse> getYearlySalesReport(@RequestParam int year) {
        return ResponseEntity.ok(adminService.getYearlySalesReport(year));
    }

    @GetMapping("/reports/sales-by-city")
    public ResponseEntity<AdminReportResponse> getSalesByCityReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(adminService.getSalesByCityReport(
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        ));
    }

    @GetMapping("/reports/best-sellers")
    public ResponseEntity<List<ProductResponse>> getBestSellers(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(adminService.getBestSellingProducts(limit));
    }

    @GetMapping("/reports/export")
    public ResponseEntity<String> exportReport(
            @RequestParam String reportType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        AdminReportResponse report;
        switch (reportType) {
            case "daily":
                report = adminService.getDailySalesReport(LocalDate.parse(startDate));
                break;
            case "monthly":
                String[] parts = startDate.split("-");
                report = adminService.getMonthlySalesReport(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1])
                );
                break;
            case "yearly":
                report = adminService.getYearlySalesReport(Integer.parseInt(startDate));
                break;
            case "city":
                report = adminService.getSalesByCityReport(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                );
                break;
            default:
                return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adminService.exportReportToTxt(report));
    }

    // ===== CUSTOMER MANAGEMENT =====

    @GetMapping("/users")
    public ResponseEntity<Page<AdminCustomerResponse>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getAllCustomers(pageable));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<AdminCustomerResponse> getCustomerDetails(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getCustomerDetails(id));
    }
}