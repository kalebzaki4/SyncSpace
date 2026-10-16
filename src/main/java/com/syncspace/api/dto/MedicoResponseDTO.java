package com.syncspace.api.dto;

import com.syncspace.api.enums.Especialidade;
import com.syncspace.api.model.Medico;

import java.util.List;

public record MedicoResponseDTO(
        Long id,
        String crm,
        List<Especialidade> especialidades
) {
    public MedicoResponseDTO(Medico medico) {
        this(
                medico.getId(),
                medico.getCrm(),
                medico.getEspecialidades().stream().sorted().toList()
        );
    }
}
