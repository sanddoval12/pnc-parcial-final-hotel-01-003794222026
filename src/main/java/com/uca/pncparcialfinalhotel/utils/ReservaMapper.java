package com.uca.pncparcialfinalhotel.utils;

import com.uca.pncparcialfinalhotel.dto.response.ReservaDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Habitacion;
import com.uca.pncparcialfinalhotel.entities.Reserva;
import com.uca.pncparcialfinalhotel.entities.Usuario;

import java.time.LocalDate;

public class ReservaMapper {

    public static Reserva toEntity(Usuario huesped, Habitacion habitacion, LocalDate fechaInicio, LocalDate fechaFin) {
        return Reserva.builder()
                .huesped(huesped)
                .habitacion(habitacion)
                .fechaInicio(fechaInicio)
                .fechaFin(fechaFin)
                .build();
    }

    public static ReservaDTOResponse toResponse(Reserva reserva) {
        return new ReservaDTOResponse(
                reserva.getId(),
                reserva.getHuesped().getId(),
                reserva.getHuesped().getUsername(),
                reserva.getHabitacion().getId(),
                reserva.getHabitacion().getNumero(),
                reserva.getHabitacion().getSucursal().getId(),
                reserva.getHabitacion().getSucursal().getNombre(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getEstado(),
                reserva.getFechaCreacion()
        );
    }
}
