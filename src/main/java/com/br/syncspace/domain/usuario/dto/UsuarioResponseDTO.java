package com.br.syncspace.domain.usuario.dto;

import com.br.syncspace.domain.usuario.Usuario;

public record UsuarioResponseDTO(
        Long id,
        String email,
        String nome,
        String role
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getNome(),
                usuario.getRole().name()
        );
    }
}