package com.br.syncspace.domain.sala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record SalaRequestDTO(
        @NotBlank(message = "O nome da sala é obrigatório.")
        String nome,

        @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
        String descricao,

        @NotNull(message = "A capacidade da sala é obrigatória.")
        @Positive(message = "A capacidade deve ser maior que zero.")
        Integer capacidadeInicial) {
}
