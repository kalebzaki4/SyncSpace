package com.syncspace.api.dto;

import com.syncspace.api.enums.Alergias;
import com.syncspace.api.enums.CondicoesCronicas;
import com.syncspace.api.enums.MedicamentosEmUsoContinuo;
import com.syncspace.api.enums.TipoSanguineo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public record PacienteRequestDTO(
        @Valid
        UsuarioRequestDTO usuario,
        @NotBlank
        TipoSanguineo tipoSanguineo,
        @NotBlank
        Alergias alergias,
        @NotBlank
        CondicoesCronicas doencasCronicas,
        @NotBlank
        MedicamentosEmUsoContinuo medicamentos,
        @NotBlank
        String contatoDeEmergencia,
        @NotBlank
        String telefoneContatoEmergencia,
        @NotBlank
        String prontuario
) {
}
