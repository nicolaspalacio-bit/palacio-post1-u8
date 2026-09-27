package com.example.auditoria.adapter.in.web.dto;

import com.example.auditoria.domain.valueobject.Severidad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RegistrarHallazgoRequest(
    @NotBlank String titulo,
    String descripcion,
    @NotBlank String areaResponsable,
    @NotNull Severidad severidad,
    @NotNull LocalDate fechaDeteccion
) {}
