package com.syncspace.api.dto;

import com.syncspace.api.model.Consulta;

import java.util.Date;

public record ReservaResponseDTO(
        Long id,
        Long pacienteId,
        Long salaId,
        Long medicoId,
        Date dataHoraInicio,
        Date dataHoraFim
) {
    public ReservaResponseDTO(Consulta consulta) {
        this(
                consulta.getId(),
                consulta.getPaciente().getId(),
                consulta.getSala().getId(),
                consulta.getMedico().getId(),
                consulta.getDataHoraInicio(),
                consulta.getDataHoraFim()
        );
    }
}
