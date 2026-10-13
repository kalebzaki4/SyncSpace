package com.syncspace.api.dto;

import com.syncspace.api.model.Atendente;

public record AtendenteResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String setor
) {
    public AtendenteResponseDTO(Atendente atendente) {
        this(
                atendente.getId(),
                atendente.getUsuario().getNome(),
                atendente.getUsuario().getEmail(),
                atendente.getUsuario().getTelefone(),
                atendente.getSetor().name()
        );
    }
}
