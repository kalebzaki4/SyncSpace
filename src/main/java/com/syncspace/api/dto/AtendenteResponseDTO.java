package com.syncspace.api.dto;

import com.syncspace.api.model.Atendente;

public record AtendenteResponseDTO(
        Long id,
        String setor
) {
    public AtendenteResponseDTO(Atendente atendente) {
        this(
                atendente.getId(),
                atendente.getSetor().name()
        );
    }
}
