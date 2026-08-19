package com.carddemo.report.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter @Builder
public class ReportRequestDto {
    private final String requestId;
    private final String status;
    private final String reportType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String resolvedStartDate;
    private final String resolvedEndDate;
}
