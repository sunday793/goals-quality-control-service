package com.example.kkd_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 Not Found (for unknown run_id or unit_id)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", Map.of(
                        "code", "NOT_FOUND",
                        "message", ex.getMessage()
                )));
    }

    // 422 Unprocessable Entity (for invalid parameters)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleBadParams(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", Map.of(
                        "code", "INVALID_PARAMETER",
                        "message", "Invalid parameter: " + ex.getName()
                )));
    }

    // 500 Internal Server Error (for any other unexpected exceptions)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", Map.of(
                        "code", "INTERNAL_ERROR",
                        "message", "An unexpected error occurred"
                )));
    }
}