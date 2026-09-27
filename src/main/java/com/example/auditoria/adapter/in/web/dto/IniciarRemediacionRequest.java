package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record IniciarRemediacionRequest(
    @NotBlank String responsable,
    @NotNull LocalDate fechaLimite,
    String notas
) {}
