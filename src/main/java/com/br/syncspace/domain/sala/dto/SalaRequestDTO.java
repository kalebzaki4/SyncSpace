package com.br.syncspace.domain.sala.dto;

public record SalaRequestDTO(
        String nome,
        String descricao,
        Integer capacidade) {
}
