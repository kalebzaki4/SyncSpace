package com.syncspace.api.dto;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha
) {
}
