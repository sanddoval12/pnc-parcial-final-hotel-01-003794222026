package com.uca.pncparcialfinalhotel.utils;

import com.uca.pncparcialfinalhotel.dto.request.UsuarioDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.UsuarioDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Sucursal;
import com.uca.pncparcialfinalhotel.entities.Usuario;

public class UsuarioMapper {

    // La contraseña ya debe venir codificada (BCrypt) desde el service. El mapper no conoce
    // el PasswordEncoder a propósito: se mantiene estático y sin dependencias de Spring.
    public static Usuario toEntity(UsuarioDTORequest dto, String encodedPassword, Sucursal sucursal) {
        return Usuario.builder()
                .username(dto.username())
                .password(encodedPassword)
                .email(dto.email())
                .rol(dto.rol())
                .sucursal(sucursal)
                .build();
    }

    public static UsuarioDTOResponse toResponse(Usuario usuario) {
        return new UsuarioDTOResponse(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getSucursal() != null ? usuario.getSucursal().getId() : null,
                usuario.getSucursal() != null ? usuario.getSucursal().getNombre() : null
        );
    }
}
