package com.syncspace.api.dto;

import java.util.Date;

public record UsuarioRequestDTO(
        String nome,
        String email,
        String senha,
        String telefone,
        Date dataNascimento,
        String cpf
) {
}
