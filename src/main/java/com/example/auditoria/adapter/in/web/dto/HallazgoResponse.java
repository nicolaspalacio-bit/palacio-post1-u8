package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.entity.HallazgoAuditoria;
import com.example.auditoria.domain.valueobject.EstadoHallazgo;
import com.example.auditoria.domain.valueobject.PlanRemediacion;
import com.example.auditoria.domain.valueobject.Severidad;

import java.time.LocalDate;

public record HallazgoResponse(
    String id,
    String titulo,
    String descripcion,
    String areaResponsable,
    Severidad severidad,
    EstadoHallazgo estado,
    LocalDate fechaDeteccion,
    LocalDate fechaCierre,
    PlanResponse planRemediacion
) {
    public record PlanResponse(String responsable, LocalDate fechaLimite, String notas) {
        static PlanResponse desde(PlanRemediacion plan) {
            return plan == null ? null : new PlanResponse(plan.responsable(), plan.fechaLimite(), plan.notas());
        }
    }

    public static HallazgoResponse desde(HallazgoAuditoria h) {
        return new HallazgoResponse(
            h.getId().toString(), h.getTitulo(), h.getDescripcion(), h.getAreaResponsable(),
            h.getSeveridad(), h.getEstado(), h.getFechaDeteccion(), h.getFechaCierre(),
            PlanResponse.desde(h.getPlanRemediacion()));
    }
}
