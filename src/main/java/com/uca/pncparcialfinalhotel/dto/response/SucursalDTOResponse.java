package com.uca.pncparcialfinalhotel.dto.response;

import java.util.UUID;

public record SucursalDTOResponse(
        UUID id,
        String nombre,
        String ciudad,
        String direccion,
        String telefono
) {
}
