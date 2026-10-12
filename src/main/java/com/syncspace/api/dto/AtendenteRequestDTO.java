package com.syncspace.api.dto;

import com.syncspace.api.enums.Setores;

public record AtendenteRequestDTO(
        String cpf,
        String dataNascimento,
        String telefone,
        Setores setor,
        UsuarioRequestDTO usuario
) {
}
