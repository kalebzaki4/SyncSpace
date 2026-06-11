package com.syncspace.api.domain.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioResponseDTO(
        @NotBlank Long id, @NotBlank String nome, @Email String email) {
}
