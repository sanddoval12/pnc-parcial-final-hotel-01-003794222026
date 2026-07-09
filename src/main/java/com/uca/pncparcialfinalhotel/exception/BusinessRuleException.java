package com.uca.pncparcialfinalhotel.exception;

// Reglas de negocio (username duplicado, habitación no disponible, fechas inválidas, etc.).
// Por convención del curso: siempre mapea a 400, no a 409.
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
