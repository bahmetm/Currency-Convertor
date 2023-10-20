package com.bahmet.servlet.exchange;

import com.bahmet.exception.InvalidCurrencyConvertionPairException;
import com.bahmet.dto.ExchangeDTO;
import com.bahmet.service.CurrencyService;
import com.bahmet.service.ExchangeRateService;
import com.bahmet.utils.ResponseHandler;
import com.bahmet.utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amount = req.getParameter("amount");

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

        if (amount == null || amount.isEmpty()) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Exchange Rate amount not provided.", resp);
            return;
        }

        if (!Validator.validateCurrencyCode(baseCurrencyCode) || !Validator.validateCurrencyCode(targetCurrencyCode)) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Currency code must be in ISO 4217 format.", resp);
            return;
        }

        if (!Validator.validateRate(amount)) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid amount argument", resp);
            return;
        }

        try {

            ExchangeDTO exchangeDTO = exchangeRateService.exchange(baseCurrencyCode, targetCurrencyCode, Double.parseDouble(amount));

            ResponseHandler.sendSuccessResponse(resp, exchangeDTO);
        } catch (InvalidCurrencyConvertionPairException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_NOT_FOUND,
                    "Currencies cannot be converted.", resp);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }
}
