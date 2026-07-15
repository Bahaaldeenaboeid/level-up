package com.yourstore.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AdminReportResponse {

    private String reportType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long orderCount;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private List<DailySalesItem> breakdown;

    // Getters and Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getOrderCount() { return orderCount; }
    public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    public List<DailySalesItem> getBreakdown() { return breakdown; }
    public void setBreakdown(List<DailySalesItem> breakdown) { this.breakdown = breakdown; }

    public static class DailySalesItem {
        private LocalDate date;
        private Long orderCount;
        private BigDecimal revenue;

        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public Long getOrderCount() { return orderCount; }
        public void setOrderCount(Long orderCount) { this.orderCount = orderCount; }

        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
    }
}