package com.bahmet.servlet.currency;

import com.bahmet.exception.DatabaseException;
import com.bahmet.exception.DuplicateCurrencyException;
import com.bahmet.model.Currency;
import com.bahmet.service.CurrencyService;
import com.bahmet.utils.ResponseHandler;
import com.bahmet.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            ResponseHandler.sendSuccessResponse(resp, currencies);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String fullName = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");

        if (fullName == null || fullName.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency full name not provided.", resp);
            return;
        }

        if (code == null || code.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency code not provided.", resp);
            return;
        }

        if (sign == null || sign.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency sign not provided.", resp);
            return;
        }

        if (!Validator.validateCurrencyCode(code)) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency code must be in ISO 4217 format.", resp);
            return;
        }

        try {
            Currency currency = new Currency(code, fullName, sign);
            Currency addedCurrency = currencyService.addCurrency(currency);

            ResponseHandler.sendSuccessResponse(resp, addedCurrency);
        } catch (DuplicateCurrencyException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_CONFLICT,
                    "Currency with provided code already exist.", resp);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }
}
