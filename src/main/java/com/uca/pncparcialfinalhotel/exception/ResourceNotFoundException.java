package com.uca.pncparcialfinalhotel.exception;

// Se usa cuando se busca por id (Sucursal, Habitacion, Usuario, Reserva) y no existe. Mapea a 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
