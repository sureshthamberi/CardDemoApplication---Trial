package com.carddemo.fraud.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.fraud.dto.*;
import com.carddemo.fraud.service.FraudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/fraud")
@Tag(name = "Fraud Management", description = "Fraud mark/unmark and record retrieval")
@RequiredArgsConstructor
public class FraudController {

    private final FraudService fraudService;

    @PostMapping("/authorizations/{authorizationId}/mark")
    @Operation(summary = "Mark Fraud")
    public ResponseEntity<ApiResponse<FraudActionResponse>> mark(
            @PathVariable String authorizationId,
            @Valid @RequestBody FraudActionRequest request,
            @AuthenticationPrincipal String principal) {
        FraudActionResponse response = fraudService.markFraud(authorizationId, request, principal);
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.ok("Fraud marked successfully", response));
    }

    @PostMapping("/authorizations/{authorizationId}/unmark")
    @Operation(summary = "Unmark Fraud")
    public ResponseEntity<ApiResponse<FraudActionResponse>> unmark(
            @PathVariable String authorizationId,
            @Valid @RequestBody FraudActionRequest request,
            @AuthenticationPrincipal String principal) {
        FraudActionResponse response = fraudService.unmarkFraud(authorizationId, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Fraud unmarked successfully", response));
    }

    @GetMapping("/records/{authorizationId}")
    @Operation(summary = "Get Fraud Records for Authorization")
    public ResponseEntity<ApiResponse<List<FraudRecordDto>>> getRecords(
            @PathVariable String authorizationId) {
        return ResponseEntity.ok(ApiResponse.ok("Fraud records retrieved",
                fraudService.getFraudRecords(authorizationId)));
    }
}
