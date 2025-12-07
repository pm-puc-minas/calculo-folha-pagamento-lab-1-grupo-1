package com.payroll.web;
/*
 * Tratador global de exceções da aplicação.
 * Mapeia exceções de negócio para HTTP (400/404/409/500),
 * retorna ApiError/Envelope padronizado e logs úteis.
 * Garante respostas consistentes e previsíveis em falhas.
 */

import com.payroll.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ApiError<T>> build(HttpStatus status, String message, String path, T details) {
        ApiError<T> body = new ApiError<>(status.value(), status.getReasonPhrase(), message, path, details);
        return ResponseEntity.status(status).body(body);
    }

    @SuppressWarnings("unchecked")
    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<ApiError<Map<String, Object>>> handleInput(InputValidationException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), (Map<String, Object>) ex.getContext());
    }

    @ExceptionHandler(NotFoundBusinessException.class)
    public ResponseEntity<ApiError<Object>> handleNotFound(NotFoundBusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), ex.getContext());
    }

    @ExceptionHandler(DataIntegrityBusinessException.class)
    public ResponseEntity<ApiError<Object>> handleIntegrity(DataIntegrityBusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(DatabaseConnectionException.class)
    public ResponseEntity<ApiError<Object>> handleConnection(DatabaseConnectionException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(InternalServerBusinessException.class)
    public ResponseEntity<ApiError<Object>> handleInternal(InternalServerBusinessException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError<Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(java.util.NoSuchElementException.class)
    public ResponseEntity<ApiError<Object>> handleNoSuchElement(java.util.NoSuchElementException ex, HttpServletRequest req) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req.getRequestURI(), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError<Object>> handleFallback(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req.getRequestURI(), null);
    }
}
