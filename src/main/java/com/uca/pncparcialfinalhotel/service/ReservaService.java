package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.ReservaDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservaDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Habitacion;
import com.uca.pncparcialfinalhotel.entities.Reserva;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.entities.enums.EstadoReserva;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.ReservaRepository;
import com.uca.pncparcialfinalhotel.utils.ReservaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HabitacionService habitacionService;
    private final UsuarioService usuarioService;

    public ReservaDTOResponse crear(ReservaDTORequest dto) {
        if (dto.huespedId() == null) {
            throw new BusinessRuleException("El huésped es obligatorio");
        }
        if (!dto.fechaInicio().isBefore(dto.fechaFin())) {
            throw new BusinessRuleException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        Usuario huesped = usuarioService.buscarUsuarioOrThrow(dto.huespedId());
        Habitacion habitacion = habitacionService.buscarHabitacionOrThrow(dto.habitacionId());

        if (!habitacion.isDisponible()) {
            throw new BusinessRuleException("La habitación " + habitacion.getNumero() + " no está disponible");
        }

        habitacion.setDisponible(false);
        habitacionService.guardar(habitacion);

        Reserva reserva = ReservaMapper.toEntity(huesped, habitacion, dto.fechaInicio(), dto.fechaFin());
        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    public List<ReservaDTOResponse> listar() {
        return reservaRepository.findAll().stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public List<ReservaDTOResponse> listarPorHuesped(UUID huespedId) {
        return reservaRepository.findByHuespedId(huespedId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public List<ReservaDTOResponse> listarPorSucursal(UUID sucursalId) {
        return reservaRepository.findByHabitacionSucursalId(sucursalId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public ReservaDTOResponse obtenerPorId(UUID id) {
        return ReservaMapper.toResponse(buscarReservaOrThrow(id));
    }

    public ReservaDTOResponse confirmar(UUID id) {
        Reserva reserva = buscarReservaOrThrow(id);
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleException("Solo una reserva PENDIENTE puede confirmarse");
        }
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    public ReservaDTOResponse cancelar(UUID id) {
        Reserva reserva = buscarReservaOrThrow(id);
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BusinessRuleException("La reserva ya está cancelada");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);

        Habitacion habitacion = reserva.getHabitacion();
        habitacion.setDisponible(true);
        habitacionService.guardar(habitacion);

        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    public Reserva buscarReservaOrThrow(UUID id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada con id " + id));
    }
}
