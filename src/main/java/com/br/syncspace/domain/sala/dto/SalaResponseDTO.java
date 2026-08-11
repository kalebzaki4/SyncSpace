package com.br.syncspace.domain.sala.dto;

public record SalaResponseDTO(
        Long id,
        String nome,
        String descricao,
        Integer capacidade) {
}
