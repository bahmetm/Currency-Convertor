package com.bahmet.utils;

import com.bahmet.dto.ErrorResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseHandler {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendErrorResponse(int code, String message, HttpServletResponse response) throws IOException {
        response.setStatus(code);
        response.getWriter().write(MAPPER.writeValueAsString(new ErrorResponseDTO(message)));
    }

    public static <T> void sendSuccessResponse(HttpServletResponse resp, T object) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(MAPPER.writeValueAsString(object));
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
