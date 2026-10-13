package com.syncspace.api.dto;

import java.util.Date;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        Date dataNascimento,
        String cpf,
        String role
) {
    public UsuarioResponseDTO(com.syncspace.api.model.Usuario usuario) {
        this(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getDataNascimento(),
                usuario.getCpf(),
                usuario.getRole().name()
        );
    }
}
