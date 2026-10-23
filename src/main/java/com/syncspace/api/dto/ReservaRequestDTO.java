package com.syncspace.api.dto;

import com.syncspace.api.enums.StatusConsulta;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ReservaRequestDTO(
        @NotNull Long pacienteId,
        @NotNull Long salaId,
        @NotNull Long medicoId,
        @NotNull Date dataHoraInicio,
        @NotNull Date dataHoraFim,
        @NotNull StatusConsulta statusConsulta
        ) {
}
