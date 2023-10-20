package com.bahmet.servlet.exchange;

import com.bahmet.exception.InvalidCurrencyCodeException;
import com.bahmet.model.ExchangeRate;
import com.bahmet.service.ExchangeRateService;
import com.bahmet.utils.RequestExtractor;
import com.bahmet.utils.ResponseHandler;
import com.bahmet.utils.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getMethod().equalsIgnoreCase("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String currencyCodes = RequestExtractor.extractRequest(req);

            if (!Validator.validateCurrencyCodes(currencyCodes)) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Currency codes not provided.", resp);
                return;
            }

            ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCurrencyCodes(currencyCodes.substring(0, 3), currencyCodes.substring(3));

            if (exchangeRate == null) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_NOT_FOUND,
                        "Exchange Rate not found.", resp);
                return;
            }

            ResponseHandler.sendSuccessResponse(resp, exchangeRate);
        } catch (IllegalArgumentException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String currencyCodes = RequestExtractor.extractRequest(req);

            if (!Validator.validateCurrencyCodes(currencyCodes)) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Currency codes not provided.", resp);
                return;
            }

            String requestBody = req.getReader().readLine();

            if (requestBody == null || requestBody.isEmpty()) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Rate is not provided.", resp);
                return;
            }

            if (!Validator.validatePATCHRate(requestBody)) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Rate is not provided.", resp);
                return;
            }

            String rate = requestBody.substring(5);

            ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(currencyCodes.substring(0, 3), currencyCodes.substring(3), Double.parseDouble(rate));

            ResponseHandler.sendSuccessResponse(resp, exchangeRate);
        } catch (IllegalArgumentException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }
}
