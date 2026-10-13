package com.syncspace.api.dto;

import com.syncspace.api.model.Paciente;
import com.syncspace.api.enums.TipoSanguineo;
import com.syncspace.api.enums.Alergias;
import com.syncspace.api.enums.CondicoesCronicas;
import com.syncspace.api.enums.MedicamentosEmUsoContinuo;

import java.util.Date;

public record PacienteResponseDTO(
        Long id,
        String nome,
        String email,
        String cpf,
        Date dataNascimento,
        String telefone,
        TipoSanguineo tipoSanguineo,
        Alergias alergias,
        CondicoesCronicas doencasCronicas,
        MedicamentosEmUsoContinuo medicamentos,
        String prontuario,
        String contatoDeEmergencia,
        String telefoneContatoEmergencia
) {
    public PacienteResponseDTO(Paciente paciente) {
        this(
                paciente.getId(),
                paciente.getUsuario().getNome(),
                paciente.getUsuario().getEmail(),
                paciente.getUsuario().getCpf(),
                paciente.getUsuario().getDataNascimento(),
                paciente.getUsuario().getTelefone(),
                paciente.getTipoSanguineo(),
                paciente.getAlergias(),
                paciente.getDoencasCronicas(),
                paciente.getMedicamentos(),
                paciente.getProntuario(),
                paciente.getContatoDeEmergencia(),
                paciente.getTelefoneContatoEmergencia()
        );
    }
}