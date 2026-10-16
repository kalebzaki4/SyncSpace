package com.syncspace.api.dto;

import com.syncspace.api.enums.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicoRequestDTO(
        @NotNull Long usuarioId,
        @NotBlank String crm,
        @NotNull Especialidade especialidade
) {
}
