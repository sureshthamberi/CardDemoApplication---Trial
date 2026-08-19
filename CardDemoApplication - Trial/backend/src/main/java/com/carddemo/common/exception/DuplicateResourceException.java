package com.carddemo.common.exception;

/**
 * Thrown when a duplicate resource creation is attempted.
 */
public class DuplicateResourceException extends RuntimeException {
    private final String errorCode;

    public DuplicateResourceException(String message) {
        super(message);
        this.errorCode = "CONFLICT";
    }

    public DuplicateResourceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
