package com.bahmet.service;

import com.bahmet.dao.CurrencyDAO;
import com.bahmet.dao.ExchangeRateDAO;
import com.bahmet.exception.DatabaseException;
import com.bahmet.exception.InvalidCurrencyConvertionPair;
import com.bahmet.model.Currency;
import com.bahmet.dto.ExchangeDTO;
import com.bahmet.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDAO exchangeRateDAO = new ExchangeRateDAO();
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDAO.getAllExchangeRates();
    }

    public ExchangeRate getExchangeRateByCurrencyCodes(String baseCurrencyCode, String targetCurrencyCode) {
        return exchangeRateDAO.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
    }

    public ExchangeRate addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        Currency baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode);
        if (baseCurrency == null) {
            throw new DatabaseException("There is no currency with this code in database: " + targetCurrencyCode);
        }

        Currency targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode);
        if (targetCurrency == null) {
            throw new DatabaseException("There is no currency with this code in database: " + targetCurrencyCode);
        }

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

        return exchangeRateDAO.addExchangeRate(exchangeRate);
    }

    public ExchangeRate updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate == null) {
            throw new RuntimeException("There is no Exchange Rate with this currencies.");
        }

        exchangeRate.setRate(rate);

        return exchangeRateDAO.updateExchangeRate(exchangeRate);
    }

    public ExchangeDTO exchange(String baseCurrencyCode, String targetCurrencyCode, double amount) {
        Currency baseCurrency = currencyDAO.getCurrencyByCode(baseCurrencyCode);
        Currency targetCurrency = currencyDAO.getCurrencyByCode(targetCurrencyCode);

        BigDecimal rate = getConversionRate(baseCurrencyCode, targetCurrencyCode);
        BigDecimal amt = BigDecimal.valueOf(amount);

        BigDecimal convertedAmount = rate.multiply(amt);

        return new ExchangeDTO(baseCurrency, targetCurrency, rate.doubleValue(), amount, convertedAmount.setScale(2, RoundingMode.HALF_UP).doubleValue());
    }

    private BigDecimal getConversionRate(String baseCurrencyCode, String targetCurrencyCode) {
        // Direct conversion
        ExchangeRate exchangeRate = exchangeRateDAO.getExchangeRateByCodes(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate != null) {
            return BigDecimal.valueOf(exchangeRate.getRate());
        }

        // Reversed conversion
        ExchangeRate reversedExchangeRate = exchangeRateDAO.getExchangeRateByCodes(targetCurrencyCode, baseCurrencyCode);
        if (reversedExchangeRate != null) {
            return BigDecimal.ONE.divide(BigDecimal.valueOf(reversedExchangeRate.getRate()), 10, RoundingMode.HALF_UP);
        }

        // Via USD
        ExchangeRate USDToBaseExchangeRate = exchangeRateDAO.getExchangeRateByCodes("USD", baseCurrencyCode);
        ExchangeRate USDToTargetExchangeRate = exchangeRateDAO.getExchangeRateByCodes("USD", targetCurrencyCode);

        if (USDToBaseExchangeRate != null && USDToTargetExchangeRate != null) {
            BigDecimal baseRate = BigDecimal.ONE.divide(BigDecimal.valueOf(USDToBaseExchangeRate.getRate()), 10, RoundingMode.HALF_UP);
            BigDecimal targetRate = BigDecimal.valueOf(USDToTargetExchangeRate.getRate());
            return baseRate.multiply(targetRate);
        }

        throw new InvalidCurrencyConvertionPair();
    }
}
