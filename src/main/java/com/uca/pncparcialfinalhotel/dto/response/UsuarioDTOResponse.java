package com.uca.pncparcialfinalhotel.dto.response;

import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;

import java.util.UUID;

public record UsuarioDTOResponse(
        UUID id,
        String username,
        String email,
        RolUsuario rol,
        UUID sucursalId,
        String sucursalNombre
) {
}
