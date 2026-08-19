package com.carddemo.common.exception;

/**
 * Thrown when an optimistic locking / row-version conflict is detected.
 */
public class ConcurrencyConflictException extends RuntimeException {
    private final String errorCode;

    public ConcurrencyConflictException(String message) {
        super(message);
        this.errorCode = "CONFLICT";
    }

    public ConcurrencyConflictException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
