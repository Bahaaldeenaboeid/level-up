package com.yourstore.pattern.strategy;

import com.yourstore.core.exception.InvalidRequestException;
import com.yourstore.dto.response.AdminReportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final DailyReportStrategy dailyReportStrategy;
    private final MonthlyReportStrategy monthlyReportStrategy;
    private final YearlyReportStrategy yearlyReportStrategy;
    private final CityReportStrategy cityReportStrategy;

    public ReportService(DailyReportStrategy dailyReportStrategy,
                         MonthlyReportStrategy monthlyReportStrategy,
                         YearlyReportStrategy yearlyReportStrategy,
                         CityReportStrategy cityReportStrategy) {
        this.dailyReportStrategy = dailyReportStrategy;
        this.monthlyReportStrategy = monthlyReportStrategy;
        this.yearlyReportStrategy = yearlyReportStrategy;
        this.cityReportStrategy = cityReportStrategy;
    }

    public AdminReportResponse generateReport(String type) {
        if (type == null) {
            throw new InvalidRequestException("reportType", "Type cannot be null");
        }
        String normalizedType = type.trim().toUpperCase();
        logger.info("Generating report for type {}", normalizedType);
        switch (normalizedType) {
            case "DAILY":
                return dailyReportStrategy.generate();
            case "MONTHLY":
                return monthlyReportStrategy.generate();
            case "YEARLY":
                return yearlyReportStrategy.generate();
            case "CITY":
                return cityReportStrategy.generate();
            default:
                String message = "Unsupported report type: " + type;
                logger.warn(message);
                throw new InvalidRequestException(message);
        }
    }
}
