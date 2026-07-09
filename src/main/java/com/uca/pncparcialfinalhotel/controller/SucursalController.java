package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.SucursalDTORequest;
import com.uca.pncparcialfinalhotel.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @PostMapping
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody SucursalDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(sucursalService.crear(dto))
                .message("Sucursal creada con éxito")
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(sucursalService.listar())
                .message("Sucursales encontradas")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(sucursalService.obtenerPorId(id))
                .message("Sucursal encontrada")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> actualizar(@PathVariable UUID id, @Valid @RequestBody SucursalDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(sucursalService.actualizar(id, dto))
                .message("Sucursal actualizada")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable UUID id) {
        sucursalService.eliminar(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Sucursal eliminada")
                .build());
    }
}
