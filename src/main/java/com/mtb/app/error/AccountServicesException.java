package com.mtb.app.error;

public class AccountServicesException extends Exception {

    public AccountServicesException(String message) {
        super(message);
    }

    public AccountServicesException(String message, Throwable cause) {
        super(message, cause);
    }
}
