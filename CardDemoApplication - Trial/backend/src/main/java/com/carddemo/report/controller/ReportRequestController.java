package com.carddemo.report.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.report.dto.*;
import com.carddemo.report.service.ReportRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Report Requests", description = "Report request submission and status")
@RequiredArgsConstructor
public class ReportRequestController {

    private final ReportRequestService service;

    @PostMapping("/requests")
    @Operation(summary = "Submit Report Request")
    public ResponseEntity<ApiResponse<ReportRequestDto>> create(
            @Valid @RequestBody CreateReportRequest request,
            @AuthenticationPrincipal String principal) {
        ReportRequestDto result = service.createRequest(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Report request submitted successfully", result));
    }

    @GetMapping("/requests/{requestId}")
    @Operation(summary = "Get Report Request")
    public ResponseEntity<ApiResponse<ReportRequestDto>> get(@PathVariable String requestId) {
        return ResponseEntity.ok(ApiResponse.ok("Report request retrieved", service.getRequest(requestId)));
    }
}
