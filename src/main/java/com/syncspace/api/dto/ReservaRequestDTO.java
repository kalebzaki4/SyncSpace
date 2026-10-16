package com.syncspace.api.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Date;

public record ReservaRequestDTO(
        @NotNull Long pacienteId,
        @NotNull Long salaId,
        @NotNull Long medicoId,
        @NotNull Date dataHoraInicio,
        @NotNull Date dataHoraFim
) {
}
