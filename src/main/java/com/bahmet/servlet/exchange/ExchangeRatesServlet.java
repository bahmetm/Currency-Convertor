package com.bahmet.servlet.exchange;

import com.bahmet.exception.DatabaseException;
import com.bahmet.exception.DuplicateCurrencyException;
import com.bahmet.model.ExchangeRate;
import com.bahmet.service.ExchangeRateService;
import com.bahmet.utils.ResponseHandler;
import com.bahmet.utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();
            ResponseHandler.sendSuccessResponse(resp, exchangeRates);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");

        if (baseCurrencyCode == null || baseCurrencyCode.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Exchange Rate base currency code not provided.", resp);
            return;
        }

        if (targetCurrencyCode == null || targetCurrencyCode.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Exchange Rate target currency code not provided.", resp);
            return;
        }

        if (rate == null || rate.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Exchange Rate rate not provided.", resp);
            return;
        }

        if (!Validator.validateCurrencyCode(baseCurrencyCode) || !Validator.validateCurrencyCode(targetCurrencyCode)) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency code must be in ISO 4217 format.", resp);
            return;
        }

        if (!Validator.validateRate(rate)) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid rate argument", resp);
            return;
        }

        try {
            ResponseHandler.sendSuccessResponse(resp, exchangeRateService.addExchangeRate(baseCurrencyCode, targetCurrencyCode, Double.parseDouble(rate)));
        } catch (DuplicateCurrencyException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_CONFLICT,
                    "Currency with provided code already exist.", resp);
        } catch (DatabaseException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_CONFLICT,
                    e.getMessage(), resp);
        }
        catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }
}
