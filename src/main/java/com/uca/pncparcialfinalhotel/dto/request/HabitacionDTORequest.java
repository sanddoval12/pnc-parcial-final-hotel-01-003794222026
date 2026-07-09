package com.uca.pncparcialfinalhotel.dto.request;

import com.uca.pncparcialfinalhotel.entities.enums.TipoHabitacion;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record HabitacionDTORequest(

        @NotBlank(message = "El número de habitación es obligatorio")
        String numero,

        @NotNull(message = "El tipo de habitación es obligatorio")
        TipoHabitacion tipo,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal precio,

        boolean disponible,

        @NotNull(message = "La sucursal es obligatoria")
        UUID sucursalId
) {
}
