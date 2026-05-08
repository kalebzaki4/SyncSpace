package com.syncspace.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.syncspace.api.model.Sala;

import java.time.LocalDateTime;

public record DadosSala(
        Long id,
        String nome,
        String descricao,
        Integer quantidade,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime dataCriacao,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime tempoExpiracao
) {
    public DadosSala(Sala sala) {
        this(sala.getId(), sala.getNome(), sala.getDescricao(), sala.getQuantidade(), sala.getDataCriacao(), sala.getTempoExpiracao());
    }
}
