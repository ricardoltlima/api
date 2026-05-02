package com.mtb.app.error;

public class DuplicateActiveCDAException extends ValidationException {

    public DuplicateActiveCDAException(String error, String code, String field, String message) {
        super(error, code, field, message);
    }
}
