package com.uca.pncparcialfinalhotel.repository;

import com.uca.pncparcialfinalhotel.entities.Habitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HabitacionRepository extends JpaRepository<Habitacion, UUID> {

    List<Habitacion> findBySucursalId(UUID sucursalId);

    List<Habitacion> findBySucursalIdAndDisponibleTrue(UUID sucursalId);
}
