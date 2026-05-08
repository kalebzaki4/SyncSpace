package com.syncspace.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosLogin(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6)
        String senha) {
}
