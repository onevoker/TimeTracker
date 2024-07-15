package com.onevoker.timetracker.controllers.exceptionHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevoker.timetracker.dto.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class UnauthorizedHandler implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String ENCODING = "UTF-8";
    private static final HttpStatus HTTP_STATUS_RESPONSE = HttpStatus.UNAUTHORIZED;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        sendErrorResponse(response, authException);
    }

    private void sendErrorResponse(HttpServletResponse response, AuthenticationException authException) throws IOException {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
                .exceptionName(authException.getClass().getSimpleName())
                .exceptionMessage(authException.getMessage())
                .code(HTTP_STATUS_RESPONSE.toString())
                .build();

        String json = objectMapper.writeValueAsString(errorResponse);

        response.setStatus(HTTP_STATUS_RESPONSE.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(ENCODING);
        response.getWriter().write(json);
    }
}
