package com.carddemo.common.exception;

/**
 * Thrown when authentication fails (wrong credentials / user not found).
 */
public class AuthenticationException extends RuntimeException {
    private final String errorCode;

    public AuthenticationException(String message) {
        super(message);
        this.errorCode = "AUTHENTICATION_ERROR";
    }

    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
