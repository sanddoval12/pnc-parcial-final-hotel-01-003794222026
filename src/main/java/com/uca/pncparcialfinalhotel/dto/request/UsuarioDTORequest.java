package com.uca.pncparcialfinalhotel.dto.request;

import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UsuarioDTORequest(

        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        String email,

        @NotNull(message = "El rol es obligatorio")
        RolUsuario rol,

        // Obligatorio únicamente cuando rol = RECEPCIONISTA. Se valida en la capa de servicio (Parte III).
        UUID sucursalId
) {
}
