package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.ReservaDTORequest;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // Los 3 roles pueden crear: HUESPED reserva para sí mismo, RECEPCIONISTA/ADMIN
    // pueden reservar a nombre de un huésped (ej. en el mostrador). Ver ReservaService.crear().
    @PostMapping
    @PreAuthorize("hasAnyRole('HUESPED', 'RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> crear(
            @Valid @RequestBody ReservaDTORequest dto,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.crear(dto, usuarioAutenticado))
                .message("Reserva creada con éxito")
                .build());
    }

    // Ver TODAS las reservas de TODAS las sucursales: solo ADMIN.
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listar())
                .message("Reservas encontradas")
                .build());
    }

    @GetMapping("/huesped/{huespedId}")
    @PreAuthorize("hasAnyRole('HUESPED', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> listarPorHuesped(
            @PathVariable UUID huespedId,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listarPorHuesped(huespedId, usuarioAutenticado))
                .message("Reservas del huésped encontradas")
                .build());
    }

    @GetMapping("/sucursal/{sucursalId}")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> listarPorSucursal(
            @PathVariable UUID sucursalId,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listarPorSucursal(sucursalId, usuarioAutenticado))
                .message("Reservas de la sucursal encontradas")
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HUESPED', 'RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> obtenerPorId(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.obtenerPorId(id, usuarioAutenticado))
                .message("Reserva encontrada")
                .build());
    }

    // Opción B en acción: solo RECEPCIONISTA (de la sucursal de la habitación) o ADMIN.
    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> confirmar(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.confirmar(id, usuarioAutenticado))
                .message("Reserva confirmada")
                .build());
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('HUESPED', 'RECEPCIONISTA', 'ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> cancelar(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuarioAutenticado) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.cancelar(id, usuarioAutenticado))
                .message("Reserva cancelada")
                .build());
    }
}
