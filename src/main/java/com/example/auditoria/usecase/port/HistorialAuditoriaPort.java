package com.example.auditoria.usecase.port;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;

import java.util.List;

/**
 * Bitacora de cambios de estado. Un solo puerto para escribir y leer, pero la escritura
 * es unicamente de insercion: no existe operacion para modificar ni eliminar un registro.
 */
public interface HistorialAuditoriaPort {
    void registrar(HallazgoId hallazgoId, EstadoHallazgo anterior, EstadoHallazgo nuevo,
                   String motivo, String actor);
    List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId);
}
