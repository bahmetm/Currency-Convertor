package com.bahmet.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

public class RequestExtractor {
    public static String extractRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Arguments not provided.");
        }

        return pathInfo.substring(1).toUpperCase();
    }

    public static HashMap<String, String> extractCurrencyCodesFromRequest(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Currency code not provided");
        }

        HashMap<String, String> currencyCodes = new HashMap<>();
        currencyCodes.put("baseCurrencyCode", pathInfo.substring(1, 4));
        currencyCodes.put("targetCurrencyCode", pathInfo.substring(4));
        return currencyCodes;
    }
}
