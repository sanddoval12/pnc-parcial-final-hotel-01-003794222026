package com.uca.pncparcialfinalhotel.exception;

// Se lanza cuando un usuario autenticado tiene el ROL correcto (ya pasó @PreAuthorize)
// pero el RECURSO puntual no le pertenece: un Recepcionista de otra sucursal, o un
// Huésped intentando tocar la reserva de alguien más. Es la pieza central de la regla
// de negocio no trivial (README, sección 2.3, Opción B). Mapea a 403, no a 400/404,
// porque el recurso sí existe y el rol sí es válido: lo que falta es el permiso puntual.
public class RecursoNoAutorizadoException extends RuntimeException {
    public RecursoNoAutorizadoException(String message) {
        super(message);
    }
}
