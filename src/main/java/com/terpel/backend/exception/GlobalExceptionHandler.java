package com.terpel.backend.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/** Manejador global de excepciones para la aplicación. */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error,
            String message, HttpServletRequest request, List<String> details) {
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(error)
                .message(message)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .details(details)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
            HttpServletRequest request) {
        log.warn("Recurso no encontrado: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(
            DuplicateResourceException ex, HttpServletRequest request) {
        log.warn("Recurso duplicado: {} | Path: {}", ex.getMessage(), request.getRequestURI());
        return build(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request, null);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        log.warn("Error de validación: {} | Path: {}", details, request.getRequestURI());
        return build(HttpStatus.BAD_REQUEST, "Bad Request", "Error de validación en los datos enviados",
                request, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado: {} | Path: {}", ex.getMessage(), request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.", request, null);
    }

}