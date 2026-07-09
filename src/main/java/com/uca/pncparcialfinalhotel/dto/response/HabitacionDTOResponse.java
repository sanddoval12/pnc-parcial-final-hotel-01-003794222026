package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.entities.enums.TipoHabitacion;

import java.math.BigDecimal;
import java.util.UUID;

public record HabitacionDTOResponse(
        UUID id,
        String numero,
        TipoHabitacion tipo,
        BigDecimal precio,
        boolean disponible,
        UUID sucursalId,
        String sucursalNombre
) {
}
