package com.example.auditoria.usecase;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.dobles.HistorialEnMemoria;
import com.example.auditoria.usecase.dobles.RepositorioEnMemoria;
import com.example.auditoria.usecase.impl.CerrarHallazgoService;
import com.example.auditoria.usecase.impl.ConsultarHallazgoService;
import com.example.auditoria.usecase.impl.IniciarRemediacionService;
import com.example.auditoria.usecase.impl.ReabrirHallazgoService;
import com.example.auditoria.usecase.impl.RegistrarHallazgoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CicloDeVidaHallazgoTest {

    private RepositorioEnMemoria repo;
    private HistorialEnMemoria historial;
    private RegistrarHallazgoUseCase registrar;
    private IniciarRemediacionUseCase iniciar;
    private CerrarHallazgoUseCase cerrar;
    private ReabrirHallazgoUseCase reabrir;
    private ConsultarHallazgoUseCase consultar;

    @BeforeEach
    void preparar() {
        repo = new RepositorioEnMemoria();
        historial = new HistorialEnMemoria();
        registrar = new RegistrarHallazgoService(repo);
        iniciar = new IniciarRemediacionService(repo, historial);
        cerrar = new CerrarHallazgoService(repo, historial);
        reabrir = new ReabrirHallazgoService(repo, historial);
        consultar = new ConsultarHallazgoService(repo);
    }

    private HallazgoId registrarUno() {
        return registrar.ejecutar("Contrasenas por defecto", "Servidor QA", "Infraestructura",
            Severidad.ALTA, LocalDate.of(2026, 8, 1));
    }

    @Test
    @DisplayName("Registrar persiste un hallazgo ABIERTO")
    void registrarPersisteAbierto() {
        HallazgoId id = registrarUno();
        assertEquals(EstadoHallazgo.ABIERTO, consultar.buscarPorId(id).getEstado());
    }

    @Test
    @DisplayName("El ciclo completo respeta la maquina de estados")
    void cicloCompleto() {
        HallazgoId id = registrarUno();
        iniciar.ejecutar(id, "Equipo de Infraestructura", LocalDate.of(2026, 8, 20), "Rotar credenciales", "ana");
        assertEquals(EstadoHallazgo.EN_REMEDIACION, consultar.buscarPorId(id).getEstado());
        cerrar.ejecutar(id, "ana");
        assertEquals(EstadoHallazgo.CERRADO, consultar.buscarPorId(id).getEstado());
        reabrir.ejecutar(id, "Las credenciales por defecto reaparecieron", "luis");
        assertEquals(EstadoHallazgo.REABIERTO, consultar.buscarPorId(id).getEstado());
    }

    @Test
    @DisplayName("Cerrar sin remediacion falla y no altera el hallazgo")
    void cerrarSinRemediacion() {
        HallazgoId id = registrarUno();
        assertThrows(IllegalStateException.class, () -> cerrar.ejecutar(id, "ana"));
        assertEquals(EstadoHallazgo.ABIERTO, consultar.buscarPorId(id).getEstado());
    }

    @Test
    @DisplayName("Reabrir exige un motivo")
    void reabrirExigeMotivo() {
        HallazgoId id = registrarUno();
        iniciar.ejecutar(id, "Equipo", LocalDate.of(2026, 8, 20), null, "ana");
        cerrar.ejecutar(id, "ana");
        assertThrows(IllegalArgumentException.class, () -> reabrir.ejecutar(id, "  ", "ana"));
        assertEquals(EstadoHallazgo.CERRADO, consultar.buscarPorId(id).getEstado());
    }

    @Test
    @DisplayName("Operar sobre un hallazgo inexistente lanza HallazgoNotFoundException")
    void hallazgoInexistente() {
        HallazgoId desconocido = HallazgoId.nuevo();
        assertThrows(HallazgoNotFoundException.class, () -> consultar.buscarPorId(desconocido));
        assertThrows(HallazgoNotFoundException.class, () -> cerrar.ejecutar(desconocido, "ana"));
    }

    @Test
    @DisplayName("Listar devuelve todos los hallazgos registrados")
    void listarTodos() {
        registrarUno();
        registrarUno();
        assertEquals(2, consultar.listarTodos().size());
        HallazgoAuditoria primero = consultar.listarTodos().get(0);
        assertEquals("Infraestructura", primero.getAreaResponsable());
    }
}
