package com.syncspace.api.dto;

import com.syncspace.api.enums.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record MedicoRequestDTO(
        @NotNull Long usuarioId,
        @NotBlank String crm,
        @NotEmpty Set<@NotNull Especialidade> especialidades
) {
}
