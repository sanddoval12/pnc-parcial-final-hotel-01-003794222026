package com.uca.pncparcialfinalhotel.utils;

import com.uca.pncparcialfinalhotel.dto.request.HabitacionDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.HabitacionDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Habitacion;
import com.uca.pncparcialfinalhotel.entities.Sucursal;

public class HabitacionMapper {

    public static Habitacion toEntity(HabitacionDTORequest dto, Sucursal sucursal) {
        return Habitacion.builder()
                .numero(dto.numero())
                .tipo(dto.tipo())
                .precio(dto.precio())
                .disponible(dto.disponible())
                .sucursal(sucursal)
                .build();
    }

    public static HabitacionDTOResponse toResponse(Habitacion habitacion) {
        return new HabitacionDTOResponse(
                habitacion.getId(),
                habitacion.getNumero(),
                habitacion.getTipo(),
                habitacion.getPrecio(),
                habitacion.isDisponible(),
                habitacion.getSucursal().getId(),
                habitacion.getSucursal().getNombre()
        );
    }
}
