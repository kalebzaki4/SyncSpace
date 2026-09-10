package com.br.syncspace.domain.sala.dto;

import com.br.syncspace.domain.sala.Sala;

public record SalaResponseDTO(
        Long id,
        String nome,
        String descricao,
        Integer capacidadeInicial
) {
    public SalaResponseDTO(Sala sala) {
        this(
                sala != null ? sala.getId() : null,
                sala != null ? sala.getNome() : null,
                sala != null ? sala.getDescricao() : null,
                sala != null ? sala.getCapacidadeInicial() : null
        );
    }
}
