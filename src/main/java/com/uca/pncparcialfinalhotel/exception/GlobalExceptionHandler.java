package com.uca.pncparcialfinalhotel.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(BusinessRuleException ex) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    // Regla de negocio no trivial (Opción B, sección 2.3) + ownership de Huésped (sección 2.2).
    @ExceptionHandler(RecursoNoAutorizadoException.class)
    public ResponseEntity<ApiError> handleRecursoNoAutorizado(RecursoNoAutorizadoException ex) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Valor inválido",
                        (mensaje1, mensaje2) -> mensaje1
                ));
        return build(HttpStatus.BAD_REQUEST, "Error de validación", errors);
    }

    // Se dispara cuando @PreAuthorize rechaza a un usuario autenticado con el rol incorrecto.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "No tienes permisos para realizar esta acción", null);
    }

    // Se dispara en POST /auth/login cuando el username/password no coinciden.
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex) {
        return build(HttpStatus.UNAUTHORIZED, "Usuario o contraseña incorrectos", null);
    }

    private ResponseEntity<ApiError> build(HttpStatus status, String message, Object errors) {
        return new ResponseEntity<>(ApiError.builder()
                .timestamp(LocalDateTime.now())
                .code(status.value())
                .message(message)
                .errors(errors)
                .build(), status);
    }
}
