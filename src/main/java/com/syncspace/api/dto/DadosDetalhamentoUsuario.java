package com.syncspace.api.dto;

import com.syncspace.api.model.Usuario;

public record DadosDetalhamentoUsuario(Long id, String nome, String email, String senha) {
    public DadosDetalhamentoUsuario(Usuario u) {
        this(u.getId(), u.getNome(), u.getEmail(), u.getSenha()); // chama o canônico
    }
}
