package com.uca.pncparcialfinalhotel.utils;

import com.uca.pncparcialfinalhotel.dto.request.UsuarioDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.UsuarioDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Sucursal;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;

public class UsuarioMapper {

    // La contraseña ya debe venir codificada (BCrypt) desde el service. El mapper no conoce
    // el PasswordEncoder a propósito: se mantiene estático y sin dependencias de Spring.
    // El rol se recibe aparte (no se toma de dto.rol() directamente) porque el registro
    // público (/register) fuerza HUESPED sin importar lo que mande el cliente en el body.
    public static Usuario toEntity(UsuarioDTORequest dto, String encodedPassword, Sucursal sucursal, RolUsuario rol) {
        return Usuario.builder()
                .username(dto.username())
                .password(encodedPassword)
                .email(dto.email())
                .rol(rol)
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
