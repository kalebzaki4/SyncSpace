package com.br.syncspace.domain.medico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CadastroMedicoRequestDTO(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]+$") String password,
        @NotBlank String nome,
        @NotBlank String crm,
        @NotBlank String especialidade
) {
}
