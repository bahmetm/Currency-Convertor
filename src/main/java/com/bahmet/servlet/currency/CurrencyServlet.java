package com.bahmet.servlet.currency;

import com.bahmet.model.Currency;
import com.bahmet.service.CurrencyService;
import com.bahmet.utils.RequestExtractor;
import com.bahmet.utils.ResponseHandler;
import com.bahmet.utils.Validator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = RequestExtractor.extractRequest(req);

            if (!Validator.validateCurrencyCode(pathInfo)) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Currency code not provided.", resp);
                return;
            }

            Currency currency = currencyService.getCurrencyByCode(pathInfo);

            if (currency == null) {
                ResponseHandler.sendErrorResponse(HttpServletResponse.SC_NOT_FOUND,
                        "Currency not found.", resp);
                return;
            }

            ResponseHandler.sendSuccessResponse(resp, currency);
        } catch (IllegalArgumentException e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), resp);
        } catch (Exception e) {
            ResponseHandler.sendErrorResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "An internal server error occurred. Please try again later.", resp);
        }
    }
}
