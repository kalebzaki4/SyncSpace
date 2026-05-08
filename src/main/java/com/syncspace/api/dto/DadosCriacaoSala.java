package com.syncspace.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DadosCriacaoSala(
        @NotBlank String nome,
        @NotBlank String descricao,
        @NotNull @Positive Integer quantidade,
        @NotNull @Positive Integer tempoExpiracaoMinutos) {
}
