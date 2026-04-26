package com.mtb.app.error;

public class ValidationException extends RuntimeException {

    private final String error;
    private final String code;
    private final String field;

    public ValidationException(String field, String message) {
        this("Validation failed", "VALIDATION_ERROR", field, message);
    }

    public ValidationException(String error, String code, String field, String message) {
        super(message);
        this.error = error;
        this.code = code;
        this.field = field;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }
}
