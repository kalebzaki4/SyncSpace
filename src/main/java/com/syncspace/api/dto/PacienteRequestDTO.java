package com.syncspace.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record PacienteRequestDTO(
        @NotBlank
        String cpf,
        @NotBlank
        Date dataNascimento,
        @NotBlank
        String telefone,
        @Valid
        UsuarioRequestDTO usuario
) {
}
