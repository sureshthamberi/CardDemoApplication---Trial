package com.carddemo.internal.controller;

import com.carddemo.account.entity.AccountEntity;
import com.carddemo.account.repository.AccountRepository;
import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.exception.ResourceNotFoundException;
import com.carddemo.internal.dto.*;
import com.carddemo.internal.service.AuthorizationDecisionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@Tag(name = "Internal Services", description = "Authorization decision, account inquiry, and datetime services")
@RequiredArgsConstructor
public class InternalServiceController {

    private final AuthorizationDecisionService authDecisionService;
    private final AccountRepository accountRepository;

    @PostMapping("/api/v1/services/authorizations/decide")
    @Operation(summary = "Authorization Decision")
    public ResponseEntity<ApiResponse<AuthDecisionResponse>> decide(
            @Valid @RequestBody AuthDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Authorization processed", authDecisionService.decide(request)));
    }

    @PostMapping("/api/v1/services/accounts/inquiry")
    @Operation(summary = "Account Inquiry Service")
    public ResponseEntity<ApiResponse<Map<String, Object>>> accountInquiry(@RequestBody Map<String, String> body) {
        String accountId = body.getOrDefault("accountId", "");
        AccountEntity account = accountRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("ACC-404-001", "Account not found"));
        return ResponseEntity.ok(ApiResponse.ok("Account inquiry processed", Map.of(
                "accountId", account.getAccountId(),
                "accountStatus", account.getAccountStatus(),
                "currentBalance", account.getCurrentBalance())));
    }

    @GetMapping("/api/v1/services/datetime/current")
    @Operation(summary = "Current DateTime")
    public ResponseEntity<ApiResponse<Map<String, String>>> currentDateTime() {
        return ResponseEntity.ok(ApiResponse.ok("Current date time retrieved",
                Map.of("currentDateTime", Instant.now().toString())));
    }
}
