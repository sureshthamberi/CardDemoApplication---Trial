package com.carddemo.common.exception;

/**
 * Thrown when a business rule is violated.
 */
public class BusinessRuleException extends RuntimeException {
    private final String errorCode;

    public BusinessRuleException(String message) {
        super(message);
        this.errorCode = "BUSINESS_RULE";
    }

    public BusinessRuleException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
