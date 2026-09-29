package com.example.auditoria.usecase.dobles;

import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.port.CambioEstadoView;
import com.example.auditoria.usecase.port.HistorialAuditoriaPort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Doble de prueba del puerto de historial: en memoria y estrictamente append-only. */
public class HistorialEnMemoria implements HistorialAuditoriaPort {

    private final Map<String, List<CambioEstadoView>> porHallazgo = new ConcurrentHashMap<>();

    @Override
    public void registrar(HallazgoId hallazgoId, EstadoHallazgo anterior, EstadoHallazgo nuevo,
                          String motivo, String actor) {
        porHallazgo.computeIfAbsent(hallazgoId.toString(), k -> new ArrayList<>())
            .add(new CambioEstadoView(anterior.toString(), nuevo.toString(), motivo, actor, LocalDateTime.now()));
    }

    @Override
    public List<CambioEstadoView> listarPorHallazgo(HallazgoId hallazgoId) {
        return List.copyOf(porHallazgo.getOrDefault(hallazgoId.toString(), List.of()));
    }
}
