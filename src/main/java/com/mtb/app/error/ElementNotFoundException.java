package com.mtb.app.error;

public class ElementNotFoundException extends RuntimeException {

    private final String error;
    private final String code;
    private final String field;

    public ElementNotFoundException(String field, String message) {
        this("Resource not found", "NOT_FOUND", field, message);
    }

    public ElementNotFoundException(String error, String code, String field, String message) {
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
