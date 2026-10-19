package com.syncspace.api.dto;

import com.syncspace.api.enums.Setores;
import jakarta.validation.constraints.NotNull;

public record AtendenteRequestDTO(
        Long usuarioId,
        @NotNull Setores setor
) {
}
