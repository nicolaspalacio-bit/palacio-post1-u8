package com.example.auditoria.domain;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.domain.valueobject.TransicionInvalidaException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** Prueba del Aggregate Root sin contenedor de Spring: el dominio se instancia con "new". */
class HallazgoAuditoriaTest {

    private static final LocalDate DETECCION = LocalDate.of(2026, 8, 1);
    private static final PlanRemediacion PLAN =
        new PlanRemediacion("Equipo de Infraestructura", LocalDate.of(2026, 8, 20), "Rotar credenciales");

    private HallazgoAuditoria nuevoHallazgo() {
        return new HallazgoAuditoria(HallazgoId.nuevo(), "Contrasenas por defecto",
            "El servidor QA usa credenciales del fabricante", "Infraestructura", Severidad.ALTA, DETECCION);
    }

    @Test
    @DisplayName("Un hallazgo recien registrado nace ABIERTO y sin plan ni fecha de cierre")
    void nacimiento() {
        HallazgoAuditoria h = nuevoHallazgo();
        assertEquals(EstadoHallazgo.ABIERTO, h.getEstado());
        assertNull(h.getPlanRemediacion());
        assertNull(h.getFechaCierre());
    }

    @Test
    @DisplayName("Iniciar remediacion cambia el estado, guarda el plan y devuelve el estado anterior")
    void iniciarRemediacion() {
        HallazgoAuditoria h = nuevoHallazgo();
        EstadoHallazgo anterior = h.iniciarRemediacion(PLAN);
        assertEquals(EstadoHallazgo.ABIERTO, anterior);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, h.getEstado());
        assertEquals(PLAN, h.getPlanRemediacion());
    }

    @Test
    @DisplayName("No se puede cerrar un hallazgo que nunca tuvo plan de remediacion")
    void cerrarSinPlan() {
        HallazgoAuditoria h = nuevoHallazgo();
        assertThrows(IllegalStateException.class, h::cerrar);
        assertEquals(EstadoHallazgo.ABIERTO, h.getEstado());
    }

    @Test
    @DisplayName("Cerrar registra la fecha de cierre")
    void cerrar() {
        HallazgoAuditoria h = nuevoHallazgo();
        h.iniciarRemediacion(PLAN);
        EstadoHallazgo anterior = h.cerrar();
        assertEquals(EstadoHallazgo.EN_REMEDIACION, anterior);
        assertEquals(EstadoHallazgo.CERRADO, h.getEstado());
        assertNotNull(h.getFechaCierre());
    }

    @Test
    @DisplayName("No se puede reabrir un hallazgo que sigue abierto")
    void reabrirAbierto() {
        HallazgoAuditoria h = nuevoHallazgo();
        assertThrows(TransicionInvalidaException.class, h::reabrir);
    }

    @Test
    @DisplayName("Reabrir limpia la fecha de cierre y permite una nueva remediacion")
    void cicloCompletoConReapertura() {
        HallazgoAuditoria h = nuevoHallazgo();
        h.iniciarRemediacion(PLAN);
        h.cerrar();
        h.reabrir();
        assertEquals(EstadoHallazgo.REABIERTO, h.getEstado());
        assertNull(h.getFechaCierre());

        PlanRemediacion segundoPlan = new PlanRemediacion("Seguridad TI", LocalDate.of(2026, 9, 15), null);
        h.iniciarRemediacion(segundoPlan);
        assertEquals(EstadoHallazgo.EN_REMEDIACION, h.getEstado());
        assertEquals(segundoPlan, h.getPlanRemediacion());
    }

    @Test
    @DisplayName("Reconstituir conserva la fecha de cierre original en lugar de recalcularla")
    void reconstituirConservaDatosHistoricos() {
        LocalDate cierreOriginal = LocalDate.of(2026, 8, 18);
        HallazgoAuditoria h = HallazgoAuditoria.reconstituir(HallazgoId.nuevo(), "Titulo", "Desc",
            "Infraestructura", Severidad.MEDIA, DETECCION, EstadoHallazgo.CERRADO, PLAN, cierreOriginal);
        assertEquals(EstadoHallazgo.CERRADO, h.getEstado());
        assertEquals(cierreOriginal, h.getFechaCierre());
    }

    @Test
    @DisplayName("Reconstituir rechaza estados incoherentes")
    void reconstituirRechazaIncoherencias() {
        assertThrows(IllegalStateException.class, () -> HallazgoAuditoria.reconstituir(HallazgoId.nuevo(),
            "Titulo", "Desc", "Area", Severidad.BAJA, DETECCION, EstadoHallazgo.EN_REMEDIACION, null, null));
        assertThrows(IllegalStateException.class, () -> HallazgoAuditoria.reconstituir(HallazgoId.nuevo(),
            "Titulo", "Desc", "Area", Severidad.BAJA, DETECCION, EstadoHallazgo.CERRADO, PLAN, null));
    }

    @Test
    @DisplayName("El titulo y el area responsable son obligatorios")
    void validacionesDeCreacion() {
        assertThrows(IllegalArgumentException.class, () -> new HallazgoAuditoria(
            HallazgoId.nuevo(), " ", "Desc", "Area", Severidad.BAJA, DETECCION));
        assertThrows(IllegalArgumentException.class, () -> new HallazgoAuditoria(
            HallazgoId.nuevo(), "Titulo", "Desc", "", Severidad.BAJA, DETECCION));
    }

    @Test
    @DisplayName("El plan exige responsable y fecha limite")
    void validacionesDelPlan() {
        assertThrows(IllegalArgumentException.class,
            () -> new PlanRemediacion(" ", LocalDate.of(2026, 8, 20), null));
        assertThrows(NullPointerException.class,
            () -> new PlanRemediacion("Equipo", null, null));
    }
}
