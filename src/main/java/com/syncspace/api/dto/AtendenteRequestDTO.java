package com.syncspace.api.dto;

import com.syncspace.api.enums.Setores;
import jakarta.validation.constraints.NotNull;

public record AtendenteRequestDTO(
        @NotNull Setores setor
) {
}
