package com.carddemo.report.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateReportRequest {
    @NotNull @Pattern(regexp = "MONTHLY|YEARLY|CUSTOM", message = "Report type must be MONTHLY, YEARLY, or CUSTOM")
    private String reportType;

    private LocalDate startDate;
    private LocalDate endDate;

    @NotNull @Pattern(regexp = "Y", message = "Confirmation must be Y")
    private String confirmation;
}
