package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.UsuarioDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.UsuarioDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Sucursal;
import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;
import com.uca.pncparcialfinalhotel.exception.BusinessRuleException;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.UsuarioRepository;
import com.uca.pncparcialfinalhotel.utils.UsuarioMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SucursalService sucursalService;
    private final PasswordEncoder passwordEncoder;

    // Endpoint público (/register): cualquier persona puede autoregistrarse, pero
    // SIEMPRE como HUESPED. El rol que venga en el body se ignora a propósito:
    // si no, cualquiera podría autoregistrarse como ADMINISTRADOR.
    public UsuarioDTOResponse registrarComoHuesped(UsuarioDTORequest dto) {
        return registrarInterno(dto, RolUsuario.HUESPED);
    }

    // Endpoint protegido (solo ADMINISTRADOR, ver @PreAuthorize en el controller):
    // aquí sí se respeta el rol del body, para poder crear otros admins o recepcionistas.
    public UsuarioDTOResponse crear(UsuarioDTORequest dto) {
        return registrarInterno(dto, dto.rol());
    }

    private UsuarioDTOResponse registrarInterno(UsuarioDTORequest dto, RolUsuario rolFinal) {
        if (usuarioRepository.existsByUsername(dto.username())) {
            throw new BusinessRuleException("El username '" + dto.username() + "' ya está en uso");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessRuleException("El email '" + dto.email() + "' ya está registrado");
        }
        if (rolFinal == null) {
            throw new BusinessRuleException("El rol es obligatorio");
        }

        Sucursal sucursal = null;
        if (rolFinal == RolUsuario.RECEPCIONISTA) {
            if (dto.sucursalId() == null) {
                throw new BusinessRuleException("Un Recepcionista debe tener una sucursal asignada");
            }
            sucursal = sucursalService.buscarSucursalOrThrow(dto.sucursalId());
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        Usuario usuario = usuarioRepository.save(UsuarioMapper.toEntity(dto, encodedPassword, sucursal, rolFinal));
        return UsuarioMapper.toResponse(usuario);
    }

    public List<UsuarioDTOResponse> listar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    public UsuarioDTOResponse obtenerPorId(UUID id) {
        return UsuarioMapper.toResponse(buscarUsuarioOrThrow(id));
    }

    public void eliminar(UUID id) {
        Usuario usuario = buscarUsuarioOrThrow(id);
        usuarioRepository.delete(usuario);
    }

    public Usuario buscarUsuarioOrThrow(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    // Usado por UserDetailsServiceImpl (login) y JwtAuthFilter (cada request autenticado).
    public Usuario buscarPorUsernameOrThrow(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + username));
    }
}
