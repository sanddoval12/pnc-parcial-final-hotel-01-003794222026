package com.uca.pncparcialfinalhotel.utils;

import com.uca.pncparcialfinalhotel.dto.request.SucursalDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.SucursalDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Sucursal;

public class SucursalMapper {

    public static Sucursal toEntity(SucursalDTORequest dto) {
        return Sucursal.builder()
                .nombre(dto.nombre())
                .ciudad(dto.ciudad())
                .direccion(dto.direccion())
                .telefono(dto.telefono())
                .build();
    }

    public static SucursalDTOResponse toResponse(Sucursal sucursal) {
        return new SucursalDTOResponse(
                sucursal.getId(),
                sucursal.getNombre(),
                sucursal.getCiudad(),
                sucursal.getDireccion(),
                sucursal.getTelefono()
        );
    }
}
