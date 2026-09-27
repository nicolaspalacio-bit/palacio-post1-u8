package com.example.auditoria.usecase.dobles;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
}
