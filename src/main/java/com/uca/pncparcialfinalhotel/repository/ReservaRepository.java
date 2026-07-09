package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, UUID> {

    List<Reserva> findByHuespedId(UUID huespedId);

    List<Reserva> findByHabitacionId(UUID habitacionId);

    // Usada en la Parte VII para validar la sucursal del Recepcionista
    List<Reserva> findByHabitacionSucursalId(UUID sucursalId);
}
