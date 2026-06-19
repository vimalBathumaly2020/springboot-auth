package com.venueelite.app.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= VALIDATION ERRORS =================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // ================= AUTH =================
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentials(
            BadCredentialsException ex, HttpServletRequest request) {
        return buildError("Invalid email or password", HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    // ================= NOT FOUND =================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI());
    }

    // ================= TOKEN =================
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidToken(
            InvalidTokenException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiErrorResponse> handleTokenExpired(
            TokenExpiredException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), HttpStatus.GONE, request.getRequestURI());
    }

    // ================= IP MISMATCH =================
    @ExceptionHandler(IpMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleIpMismatch(
            IpMismatchException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI());
    }

    // ================= CONFLICT =================
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, HttpServletRequest request) {
        return buildError(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI());
    }

    // ================= GENERIC =================
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(
            Exception ex, HttpServletRequest request) {
        return buildError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI());
    }

    // ================= HELPER =================
    private ResponseEntity<ApiErrorResponse> buildError(
            String message, HttpStatus status, String path) {
        return new ResponseEntity<>(
                ApiErrorResponse.builder()
                        .message(message)
                        .status(status.value())
                        .timestamp(LocalDateTime.now())
                        .path(path)
                        .build(),
                status
        );
    }
}