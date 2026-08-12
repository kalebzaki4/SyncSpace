package com.br.syncspace.domain.reserva.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record ReservaRequestDTO(
        Long id,

        @NotBlank(message = "O nome do paciente é obrigatório.")
        String nomeDoPaciente,

        @Size(max = 255, message = "A descrição não pode exceder 255 caracteres.")
        String descricao,

        @NotNull(message = "A data e hora de início são obrigatórias.")
        @Future(message = "A data de início deve ser uma data futura.")
        LocalDateTime dataHoraInicio,

        @NotNull(message = "A data e hora de fim são obrigatórias.")
        @Future(message = "A data de fim deve ser uma data futura.")
        LocalDateTime dataHoraFim,

        @NotNull(message = "O ID da sala é obrigatório.")
        Long salaId,

        @NotNull(message = "A quantidade de pessoas é obrigatória.")
        @Positive(message = "A quantidade de pessoas deve ser maior que zero.")
        Integer quantidadePessoas
) {}