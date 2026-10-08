package com.syncspace.api.dto;

import com.syncspace.api.enums.Especialidade;
import com.syncspace.api.model.Medico;

public record MedicoResponseDTO(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade
) {
    public MedicoResponseDTO(Medico medico) {
        this(
                medico.getId(),
                medico.getUsuario().getNome(),
                medico.getUsuario().getEmail(),
                medico.getCrm(),
                medico.getEspecialidade()
        );
    }
}
