package com.example.auditoria.usecase.impl;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.HallazgoId;
import com.example.auditoria.usecase.HallazgoNotFoundException;
import com.example.auditoria.usecase.ReabrirHallazgoUseCase;
import com.example.auditoria.usecase.port.HallazgoRepositoryPort;

public class ReabrirHallazgoService implements ReabrirHallazgoUseCase {

    private final HallazgoRepositoryPort repo;

    public ReabrirHallazgoService(HallazgoRepositoryPort repo) {
        this.repo = repo;
    }

    @Override
    public void ejecutar(HallazgoId id, String motivo) {
        // Regla de la aplicacion: reabrir un hallazgo exige justificarlo.
        if (motivo == null || motivo.isBlank())
            throw new IllegalArgumentException("El motivo de reapertura es obligatorio");
        HallazgoAuditoria hallazgo = repo.buscarPorId(id)
            .orElseThrow(() -> new HallazgoNotFoundException(id));
        hallazgo.reabrir();
        repo.guardar(hallazgo);
    }
}
