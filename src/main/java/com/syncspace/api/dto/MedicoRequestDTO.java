package com.syncspace.api.dto;

import com.syncspace.api.enums.Especialidade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record MedicoRequestDTO(
        @NotBlank String crm,
        @NotBlank Especialidade especialidade,
        @Valid UsuarioRequestDTO usuario
) {
}
