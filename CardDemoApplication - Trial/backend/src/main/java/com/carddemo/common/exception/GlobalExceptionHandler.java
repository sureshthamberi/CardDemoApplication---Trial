package com.carddemo.common.exception;

import com.carddemo.common.dto.ApiResponse;
import com.carddemo.common.dto.ApiResponse.ErrorType;
import com.carddemo.common.dto.ApiResponse.FieldError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Centralised exception handler translating domain exceptions into consistent
 * API error envelopes per the LLD Section 4.1.8 / 4.1.9.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ------------------------------------------------------------------
    // Spring Validation — @Valid failures
    // ------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> FieldError.builder()
                        .field(fe.getField())
                        .code(fe.getCode())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        log.warn("Validation failed: {}", fieldErrors);
        return ResponseEntity.badRequest().body(
                ApiResponse.validationError("VAL-001", "Validation failed", fieldErrors));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleBind(BindException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> FieldError.builder()
                        .field(fe.getField())
                        .code(fe.getCode())
                        .message(fe.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(
                ApiResponse.validationError("VAL-002", "Validation failed", fieldErrors));
    }

    // ------------------------------------------------------------------
    // Domain exceptions
    // ------------------------------------------------------------------
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        log.warn("Authentication failure: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.error(ErrorType.AUTHENTICATION_ERROR, ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.info("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error(ErrorType.NOT_FOUND, ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessRule(BusinessRuleException ex) {
        log.info("Business rule violation: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(
                ApiResponse.error(ErrorType.BUSINESS_RULE_ERROR, ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateResourceException ex) {
        log.info("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(ErrorType.CONFLICT, ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(ConcurrencyConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConcurrency(ConcurrencyConflictException ex) {
        log.warn("Concurrency conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.error(ErrorType.CONFLICT, ex.getErrorCode(), ex.getMessage()));
    }

    // ------------------------------------------------------------------
    // Spring Security — access denied
    // ------------------------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.error(ErrorType.AUTHORIZATION_ERROR, "AUTH-403", "Access denied"));
    }

    // ------------------------------------------------------------------
    // Type mismatch
    // ------------------------------------------------------------------
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest().body(
                ApiResponse.error(ErrorType.VALIDATION_ERROR, "VAL-003",
                        "Invalid value for parameter: " + ex.getName()));
    }

    // ------------------------------------------------------------------
    // Catch-all
    // ------------------------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error(ErrorType.SYSTEM_ERROR, "SYS-500", "An internal error occurred"));
    }
}
