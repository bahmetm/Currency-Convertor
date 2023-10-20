package com.bahmet.service;

import com.bahmet.dao.CurrencyDAO;
import com.bahmet.exception.InvalidCurrencyCodeException;
import com.bahmet.model.Currency;
import com.bahmet.utils.Validator;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {
    private final CurrencyDAO currencyDAO = new CurrencyDAO();

    public List<Currency> getAllCurrencies() {
        return currencyDAO.getAllCurrencies();
    }

    public Currency getCurrencyByCode(String code) {
        return currencyDAO.getCurrencyByCode(code);
    }

    public Currency addCurrency(Currency currency) {
        return currencyDAO.addCurrency(currency);
    }
}
