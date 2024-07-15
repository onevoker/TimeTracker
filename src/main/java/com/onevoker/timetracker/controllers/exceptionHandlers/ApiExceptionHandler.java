package com.onevoker.timetracker.controllers.exceptionHandlers;

import com.onevoker.timetracker.dto.ApiErrorResponse;
import com.onevoker.timetracker.exceptions.ApiException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        HttpStatusCode statusCode = exception.getStatusCode();

        return ResponseEntity.status(statusCode)
                .body(getApiErrorResponseFromDefaultException(exception, statusCode));
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(HandlerMethodValidationException exception) {
        HttpStatusCode statusCode = exception.getStatusCode();

        return ResponseEntity.status(statusCode)
                .body(getApiErrorResponseFromDefaultException(exception, statusCode));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getStatusCode())
                .body(getApiErrorResponseFromApiException(exception));
    }

    private ApiErrorResponse getApiErrorResponseFromApiException(ApiException exception) {
        return ApiErrorResponse.builder()
                .exceptionName(exception.getClass().getSimpleName())
                .exceptionMessage(exception.getMessage())
                .code(exception.getStatusCode().toString())
                .build();
    }

    private ApiErrorResponse getApiErrorResponseFromDefaultException(Exception exception, HttpStatusCode statusCode) {
        String exceptionName = exception.getClass().getSimpleName();
        String exceptionMessage = exception.getMessage();

        return ApiErrorResponse.builder()
                .exceptionName(exceptionName)
                .exceptionMessage(exceptionMessage)
                .code(statusCode.toString())
                .build();
    }
}
