package com.example.auditoria.domain.valueobject;

/**
 * Ciclo de vida del hallazgo. El enum conoce su propia maquina de estados:
 * ABIERTO -> EN_REMEDIACION -> CERRADO -> REABIERTO -> EN_REMEDIACION.
 */
public enum EstadoHallazgo {
    ABIERTO, EN_REMEDIACION, CERRADO, REABIERTO;

    public boolean puedeTransicionarA(EstadoHallazgo destino) {
        return switch (this) {
            case ABIERTO -> destino == EN_REMEDIACION;
            case EN_REMEDIACION -> destino == CERRADO;
            case CERRADO -> destino == REABIERTO;
            case REABIERTO -> destino == EN_REMEDIACION;
        };
    }
}
