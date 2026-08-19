package com.carddemo.reference.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.dto.PageResponse;
import com.carddemo.reference.dto.*;
import com.carddemo.reference.service.TransactionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reference/transaction-types")
@Tag(name = "Transaction Type Reference", description = "Admin transaction type reference data maintenance")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class TransactionTypeController {

    private final TransactionTypeService service;

    @GetMapping
    @Operation(summary = "List Transaction Types")
    public ResponseEntity<ApiResponse<PageResponse<TransactionTypeDto>>> list(
            @RequestParam(required = false) String typeCode,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(ApiResponse.ok("Transaction types retrieved",
                service.listTypes(typeCode, description, page, pageSize)));
    }

    @GetMapping("/{typeCode}")
    @Operation(summary = "Get Transaction Type")
    public ResponseEntity<ApiResponse<TransactionTypeDto>> get(@PathVariable String typeCode) {
        return ResponseEntity.ok(ApiResponse.ok("Transaction type retrieved", service.getType(typeCode)));
    }

    @PostMapping
    @Operation(summary = "Create Transaction Type")
    public ResponseEntity<ApiResponse<Map<String, String>>> create(
            @Valid @RequestBody CreateTransactionTypeRequest request,
            @AuthenticationPrincipal String principal) {
        String created = service.createType(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transaction type created successfully", Map.of("typeCode", created)));
    }

    @PutMapping("/{typeCode}")
    @Operation(summary = "Update Transaction Type")
    public ResponseEntity<ApiResponse<Map<String, Object>>> update(
            @PathVariable String typeCode,
            @Valid @RequestBody UpdateTransactionTypeRequest request,
            @AuthenticationPrincipal String principal) {
        long version = service.updateType(typeCode, request, principal);
        return ResponseEntity.ok(ApiResponse.ok("Transaction type updated successfully",
                Map.of("typeCode", typeCode, "rowVersion", version)));
    }

    @DeleteMapping("/{typeCode}")
    @Operation(summary = "Delete Transaction Type")
    public ResponseEntity<ApiResponse<Map<String, String>>> delete(
            @PathVariable String typeCode,
            @AuthenticationPrincipal String principal) {
        service.deleteType(typeCode, principal);
        return ResponseEntity.ok(ApiResponse.ok("Transaction type deleted successfully", Map.of("typeCode", typeCode)));
    }
}
