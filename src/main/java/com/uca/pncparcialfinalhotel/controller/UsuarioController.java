package com.uca.pncparcialfinalhotel.controller;

import com.uca.pncparcialfinalhotel.dto.GeneralResponse;
import com.uca.pncparcialfinalhotel.dto.request.UsuarioDTORequest;
import com.uca.pncparcialfinalhotel.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/hotel/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // En la Parte V esto pasa a vivir junto a /auth, pero por ahora lo dejamos aquí
    // para poder crear usuarios de prueba (admin, recepcionista, huésped) desde Bruno.
    @PostMapping("/register")
    public ResponseEntity<GeneralResponse> registrar(@Valid @RequestBody UsuarioDTORequest dto) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.registrar(dto))
                .message("Usuario registrado con éxito")
                .build());
    }

    @GetMapping
    public ResponseEntity<GeneralResponse> listar() {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.listar())
                .message("Usuarios encontrados")
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> obtenerPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(GeneralResponse.builder()
                .data(usuarioService.obtenerPorId(id))
                .message("Usuario encontrado")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> eliminar(@PathVariable UUID id) {
        usuarioService.eliminar(id);
        return ResponseEntity.ok(GeneralResponse.builder()
                .message("Usuario eliminado")
                .build());
    }
}
