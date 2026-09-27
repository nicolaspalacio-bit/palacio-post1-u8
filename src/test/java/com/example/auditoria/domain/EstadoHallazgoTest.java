package com.example.auditoria.domain;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.example.auditoria.domain.valueobject.EstadoHallazgo.ABIERTO;
import static com.example.auditoria.domain.valueobject.EstadoHallazgo.CERRADO;
import static com.example.auditoria.domain.valueobject.EstadoHallazgo.EN_REMEDIACION;
import static com.example.auditoria.domain.valueobject.EstadoHallazgo.REABIERTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstadoHallazgoTest {

    @Test
    @DisplayName("Las cuatro transiciones del ciclo de vida son validas")
    void transicionesValidas() {
        assertTrue(ABIERTO.puedeTransicionarA(EN_REMEDIACION));
        assertTrue(EN_REMEDIACION.puedeTransicionarA(CERRADO));
        assertTrue(CERRADO.puedeTransicionarA(REABIERTO));
        assertTrue(REABIERTO.puedeTransicionarA(EN_REMEDIACION));
    }

    @Test
    @DisplayName("De las 16 combinaciones origen-destino solo 4 son validas")
    void soloCuatroDeDieciseis() {
        int validas = 0;
        for (EstadoHallazgo origen : EstadoHallazgo.values()) {
            for (EstadoHallazgo destino : EstadoHallazgo.values()) {
                if (origen.puedeTransicionarA(destino)) validas++;
            }
        }
        assertEquals(4, validas);
    }

    @Test
    @DisplayName("No se puede cerrar lo que nunca estuvo en remediacion ni reabrir lo que sigue abierto")
    void transicionesProhibidas() {
        assertFalse(ABIERTO.puedeTransicionarA(CERRADO));
        assertFalse(ABIERTO.puedeTransicionarA(REABIERTO));
        assertFalse(EN_REMEDIACION.puedeTransicionarA(REABIERTO));
    }

    @Test
    @DisplayName("Ningun estado transiciona hacia si mismo")
    void sinAutotransiciones() {
        for (EstadoHallazgo estado : EstadoHallazgo.values()) {
            assertFalse(estado.puedeTransicionarA(estado));
        }
    }
}
