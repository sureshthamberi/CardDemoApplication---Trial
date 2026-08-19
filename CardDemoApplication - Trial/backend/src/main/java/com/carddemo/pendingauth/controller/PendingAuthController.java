package com.carddemo.pendingauth.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.pendingauth.dto.*;
import com.carddemo.pendingauth.service.PendingAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pending-authorizations")
@Tag(name = "Pending Authorizations", description = "Pending authorization summary and detail")
@RequiredArgsConstructor
public class PendingAuthController {

    private final PendingAuthService service;

    @GetMapping("/accounts/{accountId}")
    @Operation(summary = "Get Pending Auth Summary for Account")
    public ResponseEntity<ApiResponse<PendingAuthSummaryResponse>> summary(
            @PathVariable String accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.ok("Pending authorizations retrieved",
                service.getAccountSummary(accountId, page, pageSize)));
    }

    @GetMapping("/{authorizationId}")
    @Operation(summary = "Get Pending Auth Detail")
    public ResponseEntity<ApiResponse<PendingAuthDetailDto>> detail(@PathVariable String authorizationId) {
        return ResponseEntity.ok(ApiResponse.ok("Pending authorization retrieved",
                service.getDetail(authorizationId)));
    }

    @GetMapping("/{authorizationId}/next")
    @Operation(summary = "Get Next Pending Auth")
    public ResponseEntity<ApiResponse<PendingAuthDetailDto>> next(@PathVariable String authorizationId) {
        return ResponseEntity.ok(ApiResponse.ok("Next pending authorization retrieved",
                service.getNext(authorizationId)));
    }
}
