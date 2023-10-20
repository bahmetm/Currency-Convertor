package com.bahmet.utils;

public class Validator {
    public static boolean validateCurrencyCode(String currencyCode) {
        return !currencyCode.isEmpty() && currencyCode.matches("^[A-Z]{3}$");
    }

    public static boolean validateCurrencyCodes(String currencyCodes) {
        return !currencyCodes.isEmpty() && currencyCodes.matches("^[A-Z]{6}$");
    }

    public static boolean validateRate(String rate) {
        return !rate.isEmpty() && rate.matches("^(?:0|[1-9]\\d*)(?:\\.\\d+)?$");
    }

    public static boolean validatePATCHRate(String requestBody) {
        return !requestBody.isEmpty() && requestBody.matches("^rate=(0\\.\\d{1,6}|[1-9]\\d{0,9}(\\.\\d{1,6})?)$");
    }
}
