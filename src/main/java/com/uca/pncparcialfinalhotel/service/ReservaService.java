package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.ReservaDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.ReservaDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Habitacion;
import com.uca.pncparcialfinalhotel.entities.Reserva;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.entities.enums.EstadoReserva;
import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;
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
    private final AutorizacionRecursoService autorizacionRecursoService;

    public ReservaDTOResponse crear(ReservaDTORequest dto, Usuario usuarioAutenticado) {
        if (!dto.fechaInicio().isBefore(dto.fechaFin())) {
            throw new BusinessRuleException("La fecha de inicio debe ser anterior a la fecha de fin");
        }

        // Un HUESPED solo puede reservar para sí mismo: se ignora dto.huespedId() y se
        // fuerza el id del usuario autenticado (token). ADMIN/RECEPCIONISTA sí pueden
        // reservar a nombre de un huésped (ej. reserva hecha en el mostrador).
        UUID huespedId = usuarioAutenticado.getRol() == RolUsuario.HUESPED
                ? usuarioAutenticado.getId()
                : dto.huespedId();

        if (huespedId == null) {
            throw new BusinessRuleException("El huésped es obligatorio");
        }

        Usuario huesped = usuarioService.buscarUsuarioOrThrow(huespedId);
        Habitacion habitacion = habitacionService.buscarHabitacionOrThrow(dto.habitacionId());

        // Opción B: un Recepcionista solo puede reservar habitaciones de su propia sucursal.
        autorizacionRecursoService.verificarSucursalDeRecepcionista(usuarioAutenticado, habitacion.getSucursal().getId());

        if (!habitacion.isDisponible()) {
            throw new BusinessRuleException("La habitación " + habitacion.getNumero() + " no está disponible");
        }

        habitacion.setDisponible(false);
        habitacionService.guardar(habitacion);

        Reserva reserva = ReservaMapper.toEntity(huesped, habitacion, dto.fechaInicio(), dto.fechaFin());
        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    // Solo ADMIN llega aquí (@PreAuthorize en el controller): ve todas, de todas las sucursales.
    public List<ReservaDTOResponse> listar() {
        return reservaRepository.findAll().stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public List<ReservaDTOResponse> listarPorHuesped(UUID huespedId, Usuario usuarioAutenticado) {
        autorizacionRecursoService.verificarPropietarioHuesped(usuarioAutenticado, huespedId);
        return reservaRepository.findByHuespedId(huespedId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public List<ReservaDTOResponse> listarPorSucursal(UUID sucursalId, Usuario usuarioAutenticado) {
        autorizacionRecursoService.verificarSucursalDeRecepcionista(usuarioAutenticado, sucursalId);
        return reservaRepository.findByHabitacionSucursalId(sucursalId).stream()
                .map(ReservaMapper::toResponse)
                .toList();
    }

    public ReservaDTOResponse obtenerPorId(UUID id, Usuario usuarioAutenticado) {
        Reserva reserva = buscarReservaOrThrow(id);
        verificarAccesoAReserva(reserva, usuarioAutenticado);
        return ReservaMapper.toResponse(reserva);
    }

    // Opción B: solo el Recepcionista de la sucursal de la habitación (o un ADMIN) puede confirmar.
    public ReservaDTOResponse confirmar(UUID id, Usuario usuarioAutenticado) {
        Reserva reserva = buscarReservaOrThrow(id);
        autorizacionRecursoService.verificarSucursalDeRecepcionista(
                usuarioAutenticado, reserva.getHabitacion().getSucursal().getId());

        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new BusinessRuleException("Solo una reserva PENDIENTE puede confirmarse");
        }
        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return ReservaMapper.toResponse(reservaRepository.save(reserva));
    }

    // El HUESPED dueño, el RECEPCIONISTA de la sucursal, o un ADMIN pueden cancelar.
    public ReservaDTOResponse cancelar(UUID id, Usuario usuarioAutenticado) {
        Reserva reserva = buscarReservaOrThrow(id);
        verificarAccesoAReserva(reserva, usuarioAutenticado);

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

    // Un ADMIN siempre pasa (ambos métodos son no-op para su rol). Un HUESPED solo pasa
    // si es el dueño. Un RECEPCIONISTA solo pasa si la reserva es de su propia sucursal.
    private void verificarAccesoAReserva(Reserva reserva, Usuario usuarioAutenticado) {
        autorizacionRecursoService.verificarPropietarioHuesped(usuarioAutenticado, reserva.getHuesped().getId());
        autorizacionRecursoService.verificarSucursalDeRecepcionista(
                usuarioAutenticado, reserva.getHabitacion().getSucursal().getId());
    }
}
