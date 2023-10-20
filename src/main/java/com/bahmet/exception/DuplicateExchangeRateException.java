package com.bahmet.exception;

public class DuplicateExchangeRateException extends DatabaseException {
    public DuplicateExchangeRateException() {
    }

    public DuplicateExchangeRateException(String message) {
        super(message);
    }
}
