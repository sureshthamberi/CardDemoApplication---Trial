package com.carddemo.payment.controller;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.payment.dto.*;
import com.carddemo.payment.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@Tag(name = "Bill Payment", description = "Bill payment preview and execution")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/bill-payments/preview/{accountId}")
    @Operation(summary = "Preview Bill Payment")
    public ResponseEntity<ApiResponse<BillPaymentPreviewDto>> preview(@PathVariable String accountId) {
        return ResponseEntity.ok(ApiResponse.ok("Payment preview retrieved", paymentService.previewPayment(accountId)));
    }

    @PostMapping("/bill-payments")
    @Operation(summary = "Create Bill Payment")
    public ResponseEntity<ApiResponse<BillPaymentResultDto>> pay(
            @Valid @RequestBody BillPaymentRequest request,
            @AuthenticationPrincipal String principal,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        BillPaymentResultDto result = paymentService.createPayment(request, principal, idempotencyKey);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Payment successful", result));
    }
}
