package com.syncspace.api.dto;

import com.syncspace.api.model.Reserva;

import java.util.Date;

public record ReservaResponseDTO(
        Long id,
        Long pacienteId,
        Long salaId,
        Long medicoId,
        Date dataHoraInicio,
        Date dataHoraFim
) {
    public ReservaResponseDTO(Reserva reserva) {
        this(
                reserva.getId(),
                reserva.getPaciente().getId(),
                reserva.getSala().getId(),
                reserva.getMedico().getId(),
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim()
        );
    }
}
