package com.example.auditoria.usecase;

import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.dobles.HistorialEnMemoria;
import com.example.auditoria.usecase.dobles.RepositorioEnMemoria;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHistorialService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import com.example.auditoria.usecase.port.CambioEstadoView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cubre la Parte 2: cada transicion exitosa deja exactamente un registro en la
 * bitacora, en orden, sin que nada pueda modificar los registros previos.
 */
class HistorialYDashboardTest {

    private RepositorioEnMemoria repo;
    private HistorialEnMemoria historial;
    private RegistrarHallazgoUseCase registrar;
    private IniciarRemediacionUseCase iniciar;
    private CerrarHallazgoUseCase cerrar;
    private ReabrirHallazgoUseCase reabrir;
    private ConsultarHistorialUseCase consultarHistorial;

    @BeforeEach
    void preparar() {
        repo = new RepositorioEnMemoria();
        historial = new HistorialEnMemoria();
        registrar = new RegistrarHallazgoService(repo);
        iniciar = new IniciarRemediacionService(repo, historial);
        cerrar = new CerrarHallazgoService(repo, historial);
        reabrir = new ReabrirHallazgoService(repo, historial);
        consultarHistorial = new ConsultarHistorialService(repo, historial);
    }

    @Test
    @DisplayName("Cada transicion exitosa agrega exactamente un registro cronologico")
    void unRegistroPorTransicion() {
        HallazgoId id = registrar.ejecutar("Hallazgo", "Desc", "Infraestructura",
            Severidad.ALTA, LocalDate.of(2026, 8, 1));

        iniciar.ejecutar(id, "Equipo", LocalDate.of(2026, 8, 20), null, "ana");
        cerrar.ejecutar(id, "ana");
        reabrir.ejecutar(id, "Reaparecio en un escaneo posterior", "luis");

        List<CambioEstadoView> eventos = consultarHistorial.ejecutar(id);
        assertEquals(3, eventos.size());
        assertEquals("ABIERTO", eventos.get(0).estadoAnterior());
        assertEquals("EN_REMEDIACION", eventos.get(0).estadoNuevo());
        assertEquals("EN_REMEDIACION", eventos.get(1).estadoAnterior());
        assertEquals("CERRADO", eventos.get(1).estadoNuevo());
        assertEquals("CERRADO", eventos.get(2).estadoAnterior());
        assertEquals("REABIERTO", eventos.get(2).estadoNuevo());
        assertEquals("luis", eventos.get(2).actor());
    }

    @Test
    @DisplayName("Un intento de transicion invalida no agrega ningun registro al historial")
    void transicionRechazadaNoDejaRastro() {
        HallazgoId id = registrar.ejecutar("Hallazgo", "Desc", "Infraestructura",
            Severidad.MEDIA, LocalDate.of(2026, 8, 1));
        assertThrows(IllegalStateException.class, () -> cerrar.ejecutar(id, "ana"));
        assertTrue(consultarHistorial.ejecutar(id).isEmpty());
    }

    @Test
    @DisplayName("Consultar el historial de un hallazgo inexistente lanza HallazgoNotFoundException")
    void historialDeHallazgoInexistente() {
        assertThrows(HallazgoNotFoundException.class, () -> consultarHistorial.ejecutar(HallazgoId.nuevo()));
    }
}
