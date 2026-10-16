package com.syncspace.api.dto;

import com.syncspace.api.enums.TipoSala;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SalaRequestDTO(
        @NotBlank String nome,
        @NotNull @Positive Integer capacidade,
        @NotNull TipoSala tipo
) {
}
