package com.example.auditoria.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ReabrirRequest(@NotBlank String motivo) {}
