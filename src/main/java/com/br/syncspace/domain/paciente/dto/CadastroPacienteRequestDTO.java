package com.br.syncspace.domain.paciente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CadastroPacienteRequestDTO(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]+$") String password,
        @NotBlank String nome,
        @NotBlank String cpf,
        @Past LocalDate dataNascimento
) {
}
