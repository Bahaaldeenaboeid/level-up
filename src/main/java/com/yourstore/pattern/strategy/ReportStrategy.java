package com.yourstore.pattern.strategy;

import com.yourstore.dto.response.AdminReportResponse;

public interface ReportStrategy {

    AdminReportResponse generate();
}
