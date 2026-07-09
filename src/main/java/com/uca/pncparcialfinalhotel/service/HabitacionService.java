package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.HabitacionDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.HabitacionDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Habitacion;
import com.uca.pncparcialfinalhotel.entities.Sucursal;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.HabitacionRepository;
import com.uca.pncparcialfinalhotel.utils.HabitacionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitacionService {

    private final HabitacionRepository habitacionRepository;
    private final SucursalService sucursalService;

    public HabitacionDTOResponse crear(HabitacionDTORequest dto) {
        Sucursal sucursal = sucursalService.buscarSucursalOrThrow(dto.sucursalId());
        Habitacion habitacion = habitacionRepository.save(HabitacionMapper.toEntity(dto, sucursal));
        return HabitacionMapper.toResponse(habitacion);
    }

    public List<HabitacionDTOResponse> listar() {
        return habitacionRepository.findAll().stream()
                .map(HabitacionMapper::toResponse)
                .toList();
    }

    public List<HabitacionDTOResponse> listarPorSucursal(UUID sucursalId) {
        return habitacionRepository.findBySucursalId(sucursalId).stream()
                .map(HabitacionMapper::toResponse)
                .toList();
    }

    public HabitacionDTOResponse obtenerPorId(UUID id) {
        return HabitacionMapper.toResponse(buscarHabitacionOrThrow(id));
    }

    public HabitacionDTOResponse actualizar(UUID id, HabitacionDTORequest dto) {
        Habitacion habitacion = buscarHabitacionOrThrow(id);
        Sucursal sucursal = sucursalService.buscarSucursalOrThrow(dto.sucursalId());

        habitacion.setNumero(dto.numero());
        habitacion.setTipo(dto.tipo());
        habitacion.setPrecio(dto.precio());
        habitacion.setDisponible(dto.disponible());
        habitacion.setSucursal(sucursal);

        return HabitacionMapper.toResponse(habitacionRepository.save(habitacion));
    }

    public void eliminar(UUID id) {
        Habitacion habitacion = buscarHabitacionOrThrow(id);
        habitacionRepository.delete(habitacion);
    }

    public Habitacion buscarHabitacionOrThrow(UUID id) {
        return habitacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habitación no encontrada con id " + id));
    }

    // Usado por ReservaService para alternar disponible=true/false al crear/cancelar una reserva.
    public Habitacion guardar(Habitacion habitacion) {
        return habitacionRepository.save(habitacion);
    }
}
