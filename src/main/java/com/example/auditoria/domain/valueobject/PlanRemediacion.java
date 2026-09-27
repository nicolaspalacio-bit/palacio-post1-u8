package com.example.auditoria.domain.valueobject;

import java.time.LocalDate;
import java.util.Objects;

/** Value Object inmutable: dos planes con los mismos valores son intercambiables. */
public record PlanRemediacion(String responsable, LocalDate fechaLimite, String notas) {

    public PlanRemediacion {
        if (responsable == null || responsable.isBlank())
            throw new IllegalArgumentException("El responsable del plan es obligatorio");
        Objects.requireNonNull(fechaLimite, "La fecha limite es obligatoria");
    }
}
