package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.entities.enums.EstadoReserva;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record ReservaDTOResponse(
        UUID id,
        UUID huespedId,
        String huespedUsername,
        UUID habitacionId,
        String habitacionNumero,
        UUID sucursalId,
        String sucursalNombre,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        EstadoReserva estado,
        LocalDateTime fechaCreacion
) {
}
