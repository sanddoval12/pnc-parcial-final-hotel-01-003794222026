package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.dto.request.SucursalDTORequest;
import com.uca.pncparcialfinalhotel.dto.response.SucursalDTOResponse;
import com.uca.pncparcialfinalhotel.entities.Sucursal;
import com.uca.pncparcialfinalhotel.exception.ResourceNotFoundException;
import com.uca.pncparcialfinalhotel.repository.SucursalRepository;
import com.uca.pncparcialfinalhotel.utils.SucursalMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public SucursalDTOResponse crear(SucursalDTORequest dto) {
        Sucursal sucursal = sucursalRepository.save(SucursalMapper.toEntity(dto));
        return SucursalMapper.toResponse(sucursal);
    }

    public List<SucursalDTOResponse> listar() {
        return sucursalRepository.findAll().stream()
                .map(SucursalMapper::toResponse)
                .toList();
    }

    public SucursalDTOResponse obtenerPorId(UUID id) {
        return SucursalMapper.toResponse(buscarSucursalOrThrow(id));
    }

    public SucursalDTOResponse actualizar(UUID id, SucursalDTORequest dto) {
        Sucursal sucursal = buscarSucursalOrThrow(id);
        sucursal.setNombre(dto.nombre());
        sucursal.setCiudad(dto.ciudad());
        sucursal.setDireccion(dto.direccion());
        sucursal.setTelefono(dto.telefono());
        return SucursalMapper.toResponse(sucursalRepository.save(sucursal));
    }

    public void eliminar(UUID id) {
        Sucursal sucursal = buscarSucursalOrThrow(id);
        sucursalRepository.delete(sucursal);
    }

    // Uso interno de HabitacionService/UsuarioService para resolver la relación por id.
    public Sucursal buscarSucursalOrThrow(UUID id) {
        return sucursalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con id " + id));
    }
}
