package com.bahmet.exception;

public class DuplicateCurrencyException extends DatabaseException {
    public DuplicateCurrencyException() {
    }

    public DuplicateCurrencyException(String message) {
        super(message);
    }
}
