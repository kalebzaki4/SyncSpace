package com.syncspace.api.dto;

public record DadosSala(
        String nome,
        String descricao,
        int quantidade,
        long tempoExpiracao
) {
}
