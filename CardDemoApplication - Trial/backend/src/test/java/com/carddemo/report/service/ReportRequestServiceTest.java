package com.carddemo.report.service;

import com.carddemo.common.exception.BusinessRuleException;
import com.carddemo.report.dto.CreateReportRequest;
import com.carddemo.report.dto.ReportRequestDto;
import com.carddemo.report.entity.ReportRequestEntity;
import com.carddemo.report.repository.ReportRequestRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportRequestService Unit Tests")
class ReportRequestServiceTest {

    @Mock ReportRequestRepository repo;
    @InjectMocks ReportRequestService service;

    @Test
    @DisplayName("MONTHLY report — resolves current month start/end dates")
    void monthly_ResolvesCurrentMonthDates() {
        when(repo.save(any(ReportRequestEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CreateReportRequest req = new CreateReportRequest();
        req.setReportType("MONTHLY"); req.setConfirmation("Y");

        ReportRequestDto result = service.createRequest(req, "USR001");

        YearMonth current = YearMonth.now();
        assertThat(result.getStartDate()).isEqualTo(current.atDay(1));
        assertThat(result.getEndDate()).isEqualTo(current.atEndOfMonth());
        assertThat(result.getStatus()).isEqualTo("SUBMITTED");
    }

    @Test
    @DisplayName("CUSTOM report — start date after end date throws BusinessRuleException")
    void custom_StartAfterEnd_ThrowsException() {
        CreateReportRequest req = new CreateReportRequest();
        req.setReportType("CUSTOM");
        req.setStartDate(LocalDate.of(2026, 8, 31));
        req.setEndDate(LocalDate.of(2026, 8, 1)); // end before start
        req.setConfirmation("Y");

        assertThatThrownBy(() -> service.createRequest(req, "USR001"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Start date must not be after end date");
    }

    @Test
    @DisplayName("CUSTOM report — missing start date throws BusinessRuleException")
    void custom_MissingStartDate_ThrowsException() {
        CreateReportRequest req = new CreateReportRequest();
        req.setReportType("CUSTOM");
        req.setEndDate(LocalDate.now());
        req.setConfirmation("Y");

        assertThatThrownBy(() -> service.createRequest(req, "USR001"))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Start date is required");
    }

    @Test
    @DisplayName("YEARLY report — resolves current year Jan 1 to Dec 31")
    void yearly_ResolvesCurrentYearDates() {
        when(repo.save(any(ReportRequestEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CreateReportRequest req = new CreateReportRequest();
        req.setReportType("YEARLY"); req.setConfirmation("Y");

        ReportRequestDto result = service.createRequest(req, "USR001");
        int year = LocalDate.now().getYear();
        assertThat(result.getStartDate()).isEqualTo(LocalDate.of(year, 1, 1));
        assertThat(result.getEndDate()).isEqualTo(LocalDate.of(year, 12, 31));
    }
}
