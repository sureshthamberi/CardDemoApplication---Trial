package com.carddemo.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Standard API success/error envelope returned by all endpoints.
 *
 * @param <T> the type of the {@code data} payload
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean success;
    private final String  message;
    private final T       data;

    // Error fields — present only on failures
    private final String           errorType;
    private final String           errorCode;
    private final java.util.List<FieldError> fieldErrors;

    private final Meta meta;

    // ------------------------------------------------------------------
    // Factory helpers
    // ------------------------------------------------------------------

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .meta(Meta.now())
                .build();
    }

    public static <T> ApiResponse<T> ok(String message, T data, String requestId) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .meta(Meta.of(requestId))
                .build();
    }

    public static ApiResponse<Void> error(String errorType, String errorCode, String message) {
        return ApiResponse.<Void>builder()
                .success(false)
                .errorType(errorType)
                .errorCode(errorCode)
                .message(message)
                .meta(Meta.now())
                .build();
    }

    public static ApiResponse<Void> validationError(
            String errorCode,
            String message,
            java.util.List<FieldError> fieldErrors) {
        return ApiResponse.<Void>builder()
                .success(false)
                .errorType(ErrorType.VALIDATION_ERROR)
                .errorCode(errorCode)
                .message(message)
                .fieldErrors(fieldErrors)
                .meta(Meta.now())
                .build();
    }

    // ------------------------------------------------------------------
    // Nested types
    // ------------------------------------------------------------------

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Meta {
        private final String requestId;
        private final String timestamp;

        public static Meta now() {
            return Meta.builder()
                    .requestId(UUID.randomUUID().toString())
                    .timestamp(Instant.now().toString())
                    .build();
        }

        public static Meta of(String requestId) {
            return Meta.builder()
                    .requestId(requestId != null ? requestId : UUID.randomUUID().toString())
                    .timestamp(Instant.now().toString())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String code;
        private final String message;
    }

    public static final class ErrorType {
        public static final String VALIDATION_ERROR     = "VALIDATION_ERROR";
        public static final String BUSINESS_RULE_ERROR  = "BUSINESS_RULE_ERROR";
        public static final String AUTHENTICATION_ERROR = "AUTHENTICATION_ERROR";
        public static final String AUTHORIZATION_ERROR  = "AUTHORIZATION_ERROR";
        public static final String NOT_FOUND            = "NOT_FOUND";
        public static final String CONFLICT             = "CONFLICT";
        public static final String PROCESSING_ERROR     = "PROCESSING_ERROR";
        public static final String SYSTEM_ERROR         = "SYSTEM_ERROR";

        private ErrorType() {}
    }
}
