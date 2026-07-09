package com.uca.pncparcialfinalhotel.service;

import com.uca.pncparcialfinalhotel.entities.Usuario;
import com.uca.pncparcialfinalhotel.entities.enums.RolUsuario;
import com.uca.pncparcialfinalhotel.exception.RecursoNoAutorizadoException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Regla de negocio no trivial (README, sección 2.3, Opción B):
 * un RECEPCIONISTA solo puede confirmar, modificar o cancelar reservas/habitaciones
 * de SU PROPIA sucursal. No basta con verificar el rol (@PreAuthorize hasRole),
 * porque dos Recepcionistas con el mismo rol pertenecen a sucursales distintas;
 * hace falta comparar la sucursal del usuario autenticado contra la sucursal del
 * recurso puntual que está intentando tocar.
 * <p>
 * También se usa (más simple) para la regla base de la sección 2.2: un HUÉSPED
 * solo puede ver/cancelar SUS PROPIAS reservas.
 * <p>
 * En ambos métodos, un ADMINISTRADOR nunca es bloqueado (acceso total, sección 2.2).
 */
@Service
public class AutorizacionRecursoService {

    public void verificarSucursalDeRecepcionista(Usuario usuarioAutenticado, UUID sucursalIdDelRecurso) {
        if (usuarioAutenticado.getRol() != RolUsuario.RECEPCIONISTA) {
            return; // ADMINISTRADOR: acceso total. HUESPED: se valida aparte con verificarPropietarioHuesped.
        }

        boolean tieneSucursalAsignada = usuarioAutenticado.getSucursal() != null;
        boolean esSuSucursal = tieneSucursalAsignada
                && usuarioAutenticado.getSucursal().getId().equals(sucursalIdDelRecurso);

        if (!esSuSucursal) {
            throw new RecursoNoAutorizadoException(
                    "El recepcionista '" + usuarioAutenticado.getUsername()
                            + "' no pertenece a la sucursal de este recurso");
        }
    }

    public void verificarPropietarioHuesped(Usuario usuarioAutenticado, UUID huespedIdDelRecurso) {
        if (usuarioAutenticado.getRol() != RolUsuario.HUESPED) {
            return; // ADMINISTRADOR/RECEPCIONISTA se validan con verificarSucursalDeRecepcionista.
        }

        if (!usuarioAutenticado.getId().equals(huespedIdDelRecurso)) {
            throw new RecursoNoAutorizadoException("Solo puedes operar sobre tus propias reservas");
        }
    }
}
