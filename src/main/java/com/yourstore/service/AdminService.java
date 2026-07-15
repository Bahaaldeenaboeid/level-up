package com.yourstore.service;

import com.yourstore.dto.response.AdminCustomerResponse;
import com.yourstore.dto.response.AdminOrderResponse;
import com.yourstore.dto.response.AdminReportResponse;
import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AdminService {

    // Orders
    Page<AdminOrderResponse> getAllOrders(Pageable pageable);

    AdminOrderResponse getOrderDetails(Long orderId);

    // Customers
    Page<AdminCustomerResponse> getAllCustomers(Pageable pageable);

    AdminCustomerResponse getCustomerDetails(Long userId);

    // Reports
    AdminReportResponse getDailySalesReport(LocalDate date);

    AdminReportResponse getMonthlySalesReport(int year, int month);

    AdminReportResponse getYearlySalesReport(int year);

    AdminReportResponse getSalesByCityReport(LocalDate startDate, LocalDate endDate);

    List<ProductResponse> getBestSellingProducts(int limit);

    String exportReportToTxt(AdminReportResponse report);
}