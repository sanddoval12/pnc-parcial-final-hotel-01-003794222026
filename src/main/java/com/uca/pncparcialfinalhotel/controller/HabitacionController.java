package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.HabitacionDTORequest;
import com.uca.pncparcialfinalhotel.service.HabitacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/habitaciones")
@RequiredArgsConstructor
public class HabitacionController {

    private final HabitacionService habitacionService;

    @PostMapping
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody HabitacionDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(habitacionService.crear(dto))
                .message("Habitación creada con éxito")
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(habitacionService.listar())
                .message("Habitaciones encontradas")
                .build());
    }

    @GetMapping("/sucursal/{sucursalId}")
    public ResponseEntity<GeneralResponse> listarPorSucursal(@PathVariable UUID sucursalId) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(habitacionService.listarPorSucursal(sucursalId))
                .message("Habitaciones de la sucursal encontradas")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(habitacionService.obtenerPorId(id))
                .message("Habitación encontrada")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody HabitacionDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(habitacionService.actualizar(id, dto))
                .message("Habitación actualizada")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable UUID id) {
        habitacionService.eliminar(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Habitación eliminada")
                .build());
    }
}
