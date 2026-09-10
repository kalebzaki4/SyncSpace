package com.br.syncspace.domain.reserva.dto;

import com.br.syncspace.domain.reserva.Reserva;

import java.time.LocalDateTime;

public record ReservaResponseDTO(
        Long id,
        String nomeDoPaciente,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFim,
        Integer quantidadePessoas,
        Long usuarioId,
        Long salaId
) {
    public ReservaResponseDTO(Reserva reserva) {
        this(
                reserva.getId(),
                reserva.getNomeDoPaciente(),
                reserva.getDescricao(),
                reserva.getDataHoraInicio(),
                reserva.getDataHoraFim(),
                reserva.getQuantidadePessoas(),
                reserva.getUsuario().getId(),
                reserva.getSala().getId()
        );
    }
}