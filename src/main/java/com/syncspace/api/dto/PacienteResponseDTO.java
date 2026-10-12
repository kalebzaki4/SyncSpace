package com.syncspace.api.dto;

import com.syncspace.api.model.Paciente;

import java.util.Date;

public record PacienteResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        Date dataNascimento,
        String telefone
) {
    public PacienteResponseDTO(Paciente paciente) {
        this(
                paciente.getId(),
                paciente.getUsuario().getNome(),
                paciente.getUsuario().getEmail(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getTelefone()
        );
    }
}
