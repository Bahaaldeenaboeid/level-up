package com.yourstore.pattern.strategy;

import com.yourstore.dto.response.AdminReportResponse;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CityReportStrategy implements ReportStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CityReportStrategy.class);

    @Override
    public AdminReportResponse generate() {
        logger.info("Generating city admin report");
        LocalDate now = LocalDate.now();
        AdminReportResponse response = new AdminReportResponse();
        response.setReportType("CITY");
        response.setStartDate(now.minusMonths(1));
        response.setEndDate(now);
        response.setOrderCount(0L);
        response.setTotalRevenue(BigDecimal.ZERO);
        response.setAverageOrderValue(BigDecimal.ZERO);
        response.setBreakdown(Collections.emptyList());
        return response;
    }
}
