package com.bahmet.exception;

public class InvalidCurrencyConvertionPairException extends RuntimeException {
    public InvalidCurrencyConvertionPairException() {
    }

    public InvalidCurrencyConvertionPairException(String message) {
        super(message);
    }
}
