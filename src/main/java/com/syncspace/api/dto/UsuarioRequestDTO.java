package com.syncspace.api.dto;

import com.syncspace.api.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record UsuarioRequestDTO(
        @NotBlank
        @Size(max = 100)
        String nome,
        @NotBlank
        @Email
        @Size(max = 100)
        String email,
        @NotBlank
        @Size(min = 8, max = 100)
        String senha,
        @NotBlank
        @Size(max = 15)
        String telefone,
        @NotNull
        Date dataNascimento,
        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,
        @NotNull
        UserRole role
) {
}
