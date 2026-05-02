package com.mtb.app.error;

public class ApiKeyException extends RuntimeException {

    private final String error;
    private final String code;
    private final String field;

    public ApiKeyException(String field, String message) {
        this("Unauthorized", "UNAUTHORIZED", field, message);
    }

    public ApiKeyException(String error, String code, String field, String message) {
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
