package com.yourstore.service.impl;

import com.yourstore.core.exception.ResourceNotFoundException;
import com.yourstore.dto.response.AdminCustomerResponse;
import com.yourstore.dto.response.AdminOrderResponse;
import com.yourstore.dto.response.AdminReportResponse;
import com.yourstore.dto.response.ProductResponse;
import com.yourstore.entity.Order;
import com.yourstore.entity.User;
import com.yourstore.enums.PaymentStatus;
import com.yourstore.mapper.AdminMapper;
import com.yourstore.repository.OrderRepository;
import com.yourstore.repository.UserRepository;
import com.yourstore.service.AdminService;
import com.yourstore.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AdminMapper adminMapper;
    private final ProductService productService;

    public AdminServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            AdminMapper adminMapper,
                            ProductService productService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.adminMapper = adminMapper;
        this.productService = productService;
    }

    @Override
    public Page<AdminOrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(adminMapper::toOrderResponse);
    }

    @Override
    public AdminOrderResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return adminMapper.toOrderResponse(order);
    }

    @Override
    public Page<AdminCustomerResponse> getAllCustomers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> {
                    List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
                    int orderCount = orders.size();
                    BigDecimal totalSpent = orders.stream()
                            .filter(o -> o.getPaymentStatus() == PaymentStatus.PAID)
                            .map(Order::getTotalAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return adminMapper.toCustomerResponse(user, orderCount, totalSpent);
                });
    }

    @Override
    public AdminCustomerResponse getCustomerDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Order> orders = orderRepository.findByUserOrderByOrderDateDesc(user);
        int orderCount = orders.size();
        BigDecimal totalSpent = orders.stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.PAID)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return adminMapper.toCustomerResponse(user, orderCount, totalSpent);
    }

    @Override
    public AdminReportResponse getDailySalesReport(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        return buildReport(orders, "DAILY", date, date);
    }

    @Override
    public AdminReportResponse getMonthlySalesReport(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        return buildReport(orders, "MONTHLY", startDate, endDate);
    }

    @Override
    public AdminReportResponse getYearlySalesReport(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        return buildReport(orders, "YEARLY", startDate, endDate);
    }

    @Override
    public AdminReportResponse getSalesByCityReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Order> orders = orderRepository.findByOrderDateBetween(start, end);
        return buildReport(orders, "CITY", startDate, endDate);
    }

    @Override
    public List<ProductResponse> getBestSellingProducts(int limit) {
        return productService.getBestSellingProducts();
    }

    @Override
    public String exportReportToTxt(AdminReportResponse report) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("  ").append(report.getReportType()).append(" SALES REPORT\n");
        sb.append("========================================\n\n");
        sb.append("Period: ").append(report.getStartDate()).append(" to ").append(report.getEndDate()).append("\n");
        sb.append("Orders: ").append(report.getOrderCount()).append("\n");
        sb.append("Total Revenue: ").append(report.getTotalRevenue()).append(" SAR\n");
        sb.append("Average Order Value: ").append(report.getAverageOrderValue()).append(" SAR\n\n");

        if (report.getBreakdown() != null && !report.getBreakdown().isEmpty()) {
            sb.append("BREAKDOWN:\n");
            sb.append("----------------------------------------\n");
            for (AdminReportResponse.DailySalesItem item : report.getBreakdown()) {
                sb.append(item.getDate()).append(" | ")
                        .append(item.getOrderCount()).append(" orders | ")
                        .append(item.getRevenue()).append(" SAR\n");
            }
        }

        sb.append("\n========================================\n");
        sb.append("Report generated on: ").append(LocalDate.now()).append("\n");
        sb.append("========================================\n");
        return sb.toString();
    }

    private AdminReportResponse buildReport(List<Order> orders, String type, LocalDate startDate, LocalDate endDate) {
        List<Order> paidOrders = orders.stream()
                .filter(o -> o.getPaymentStatus() == PaymentStatus.PAID)
                .collect(Collectors.toList());

        long orderCount = paidOrders.size();
        BigDecimal totalRevenue = paidOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgOrderValue = orderCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(orderCount), 2, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        List<AdminReportResponse.DailySalesItem> breakdown = new ArrayList<>();
        // Group by date for daily breakdown
        // Simplified — you can expand this

        return adminMapper.toReportResponse(type, startDate, endDate, orderCount, totalRevenue, breakdown);
    }
}