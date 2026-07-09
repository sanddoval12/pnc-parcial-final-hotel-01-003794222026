package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.UsuarioDTORequest;
import com.uca.pncparcialfinalhotel.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Público (ver SecurityConfiguration): cualquiera se autoregistra, siempre como HUESPED.
    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> registrarComoHuesped(@Valid @RequestBody UsuarioDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.registrarComoHuesped(dto))
                .message("Registro exitoso")
                .build());
    }

    // Protegido: solo un ADMIN puede crear usuarios con cualquier rol
    // (otro admin, un recepcionista de una sucursal específica, etc.).
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> crear(@Valid @RequestBody UsuarioDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.crear(dto))
                .message("Usuario creado con éxito")
                .build());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.listar())
                .message("Usuarios encontrados")
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.obtenerPorId(id))
                .message("Usuario encontrado")
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Usuario eliminado")
                .build());
    }
}
