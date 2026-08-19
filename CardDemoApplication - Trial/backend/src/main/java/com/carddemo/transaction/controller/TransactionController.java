package com.carddemo.transaction.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.transaction.dto.*;
import com.carddemo.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/transactions")
@Tag(name = "Transaction Management", description = "Transaction list, detail, and creation")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "List Transactions")
    public ResponseEntity<ApiResponse<PageResponse<TransactionSummaryDto>>> list(
            @RequestParam(defaultValue = "") String startTransactionId,
            @RequestParam(defaultValue = "") String accountId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.ok("Transactions retrieved",
                transactionService.listTransactions(page, pageSize, startTransactionId, accountId)));
    }

    @GetMapping("/{transactionId}")
    @Operation(summary = "Get Transaction Detail")
    public ResponseEntity<ApiResponse<TransactionDetailDto>> get(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.ok("Transaction retrieved",
                transactionService.getTransaction(transactionId)));
    }

    @PostMapping
    @Operation(summary = "Add Transaction")
    public ResponseEntity<ApiResponse<Map<String, String>>> create(
            @Valid @RequestBody CreateTransactionRequest request,
            @AuthenticationPrincipal String principal) {
        String txnId = transactionService.createTransaction(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transaction added successfully", Map.of("transactionId", txnId)));
    }

    @PostMapping("/copy-last")
    @Operation(summary = "Copy Last Transaction")
    public ResponseEntity<ApiResponse<Map<String, Object>>> copyLast(@RequestBody Map<String, String> body) {
        String accountId = body.getOrDefault("accountId", "");
        return ResponseEntity.ok(ApiResponse.ok("Last transaction copied",
                transactionService.copyLastTransaction(accountId)));
    }
}
