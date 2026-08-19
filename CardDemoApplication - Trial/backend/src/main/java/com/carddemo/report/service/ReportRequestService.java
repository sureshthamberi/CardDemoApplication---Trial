package com.carddemo.report.service;

import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.report.dto.*;
import com.carddemo.report.entity.ReportRequestEntity;
import com.carddemo.report.repository.ReportRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportRequestService {

    private final ReportRequestRepository repo;

    @Transactional
    public ReportRequestDto createRequest(CreateReportRequest req, String actor) {
        LocalDate startDate;
        LocalDate endDate;

        switch (req.getReportType()) {
            case "MONTHLY" -> {
                YearMonth current = YearMonth.now();
                startDate = current.atDay(1);
                endDate   = current.atEndOfMonth();
            }
            case "YEARLY" -> {
                int year = LocalDate.now().getYear();
                startDate = LocalDate.of(year, 1, 1);
                endDate   = LocalDate.of(year, 12, 31);
            }
            case "CUSTOM" -> {
                if (req.getStartDate() == null) throw new BusinessRuleException("RPT-VAL-001", "Start date is required for custom report");
                if (req.getEndDate()   == null) throw new BusinessRuleException("RPT-VAL-001", "End date is required for custom report");
                if (req.getStartDate().isAfter(req.getEndDate())) throw new BusinessRuleException("RPT-VAL-002", "Start date must not be after end date");
                startDate = req.getStartDate();
                endDate   = req.getEndDate();
            }
            default -> throw new BusinessRuleException("RPT-VAL-001", "Invalid report type");
        }

        String requestId = "RPT" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        ReportRequestEntity entity = ReportRequestEntity.builder()
                .requestId(requestId)
                .reportType(req.getReportType())
                .startDate(startDate)
                .endDate(endDate)
                .status("SUBMITTED")
                .submittedBy(actor)
                .submittedAt(LocalDateTime.now())
                .createdBy(actor)
                .updatedBy(actor)
                .version(0L)
                .build();

        repo.save(entity);
        log.info("Report request created: {} type: {} by: {}", requestId, req.getReportType(), actor);

        return ReportRequestDto.builder()
                .requestId(requestId)
                .status("SUBMITTED")
                .reportType(req.getReportType())
                .startDate(startDate)
                .endDate(endDate)
                .resolvedStartDate(startDate.toString())
                .resolvedEndDate(endDate.toString())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportRequestDto getRequest(String requestId) {
        ReportRequestEntity e = repo.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("RPT-404-001", "Report request not found"));
        return ReportRequestDto.builder()
                .requestId(e.getRequestId())
                .status(e.getStatus())
                .reportType(e.getReportType())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .build();
    }
}
