package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.ReservaDTORequest;
import com.uca.pncparcialfinalhotel.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    // Nota: en la Parte VI, huespedId dejará de tomarse "a ciegas" del body cuando
    // el rol autenticado sea HUESPED (se forzará al id del token). Por ahora, sin
    // seguridad todavía, se manda explícito para poder probar en Bruno.
    @PostMapping
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody ReservaDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.crear(dto))
                .message("Reserva creada con éxito")
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listar())
                .message("Reservas encontradas")
                .build());
    }

    @GetMapping("/huesped/{huespedId}")
    public ResponseEntity<GeneralResponse> listarPorHuesped(@PathVariable UUID huespedId) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listarPorHuesped(huespedId))
                .message("Reservas del huésped encontradas")
                .build());
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<GeneralResponse> listarPorSucursal(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.listarPorSucursal(sucursalId))
                .message("Reservas de la sucursal encontradas")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.obtenerPorId(id))
                .message("Reserva encontrada")
                .build());
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<GeneralResponse> confirmar(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.confirmar(id))
                .message("Reserva confirmada")
                .build());
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<GeneralResponse> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(reservaService.cancelar(id))
                .message("Reserva cancelada")
                .build());
    }
}
