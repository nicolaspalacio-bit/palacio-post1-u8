package com.example.auditoria.usecase.dobles;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.domain.valueobject.Severidad;
import com.example.auditoria.usecase.port.ConteoCategoria;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;
import com.example.auditoria.usecase.port.PromedioCategoria;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/** Doble de prueba del puerto de salida: demuestra que los casos de uso no necesitan Spring ni JPA. */
public class RepositorioEnMemoria implements HallazgoRepositoryPort {

    private final Map<HallazgoId, HallazgoAuditoria> almacen = new LinkedHashMap<>();

    @Override
    public void guardar(HallazgoAuditoria hallazgo) {
        almacen.put(hallazgo.getId(), hallazgo);
    }

    @Override
    public Optional<HallazgoAuditoria> buscarPorId(HallazgoId id) {
        return Optional.ofNullable(almacen.get(id));
    }

    @Override
    public List<HallazgoAuditoria> buscarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public List<ConteoCategoria> contarPorSeveridad() {
        return almacen.values().stream()
            .collect(Collectors.groupingBy(HallazgoAuditoria::getSeveridad, Collectors.counting()))
            .entrySet().stream()
            .map(e -> new ConteoCategoria(e.getKey().toString(), e.getValue()))
            .toList();
    }

    @Override
    public List<ConteoCategoria> contarPorEstado() {
        return almacen.values().stream()
            .collect(Collectors.groupingBy(HallazgoAuditoria::getEstado, Collectors.counting()))
            .entrySet().stream()
            .map(e -> new ConteoCategoria(e.getKey().toString(), e.getValue()))
            .toList();
    }

    @Override
    public List<PromedioCategoria> promedioDiasCierrePorArea() {
        return almacen.values().stream()
            .filter(h -> h.getEstado() == EstadoHallazgo.CERRADO && h.getFechaCierre() != null)
            .collect(Collectors.groupingBy(HallazgoAuditoria::getAreaResponsable,
                Collectors.averagingLong(h -> ChronoUnit.DAYS.between(h.getFechaDeteccion(), h.getFechaCierre()))))
            .entrySet().stream()
            .map(e -> new PromedioCategoria(e.getKey(), Math.round(e.getValue() * 10) / 10.0))
            .toList();
    }
}
