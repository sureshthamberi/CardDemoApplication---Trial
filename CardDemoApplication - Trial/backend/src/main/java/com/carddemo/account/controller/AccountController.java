package com.carddemo.account.controller;

import com.carddemo.account.dto.*;
import com.carddemo.account.service.AccountService;
import com.carddemo.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Account REST endpoints.
 * LLD Section 4.6.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "Account and customer inquiry and update")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    /** GET /api/v1/accounts/{accountId} */
    @GetMapping("/{accountId}")
    @Operation(summary = "Get Account", description = "Retrieve account with linked customer")
    public ResponseEntity<ApiResponse<AccountCompositeResponse>> getAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.ok("Account retrieved", accountService.getAccount(accountId)));
    }

    /** PUT /api/v1/accounts/{accountId} */
    @PutMapping("/{accountId}")
    @Operation(summary = "Update Account", description = "Update account and customer in one operation")
    public ResponseEntity<ApiResponse<UpdateAccountResult>> updateAccount(
            @PathVariable String accountId,
            @Valid @RequestBody UpdateAccountRequest request,
            @AuthenticationPrincipal String principal) {

        UpdateAccountResult result = accountService.updateAccount(accountId, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Changes committed", result));
    }
}
