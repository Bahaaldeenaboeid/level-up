package com.yourstore.pattern.strategy;

import com.yourstore.dto.response.AdminReportResponse;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DailyReportStrategy implements ReportStrategy {

    private static final Logger logger = LoggerFactory.getLogger(DailyReportStrategy.class);

    @Override
    public AdminReportResponse generate() {
        logger.info("Generating daily admin report");
        AdminReportResponse response = new AdminReportResponse();
        LocalDate now = LocalDate.now();
        response.setReportType("DAILY");
        response.setStartDate(now);
        response.setEndDate(now);
        response.setOrderCount(0L);
        response.setTotalRevenue(BigDecimal.ZERO);
        response.setAverageOrderValue(BigDecimal.ZERO);
        response.setBreakdown(Collections.emptyList());
        return response;
    }
}
