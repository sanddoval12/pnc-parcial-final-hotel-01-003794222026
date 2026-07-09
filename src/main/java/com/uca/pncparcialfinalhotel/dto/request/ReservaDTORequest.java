package com.uca.pncparcialfinalhotel.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record ReservaDTORequest(

        // En Parte IV se pasa explícito (aún sin seguridad). Desde Parte VI, si el rol es HUESPED,
        // el servicio ignora este valor y usa el id del usuario autenticado (JWT) por seguridad.
        UUID huespedId,

        @NotNull(message = "La habitación es obligatoria")
        UUID habitacionId,

        @NotNull(message = "La fecha de inicio es obligatoria")
        @FutureOrPresent(message = "La fecha de inicio no puede ser en el pasado")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        @Future(message = "La fecha de fin debe ser en el futuro")
        LocalDate fechaFin
) {
}
