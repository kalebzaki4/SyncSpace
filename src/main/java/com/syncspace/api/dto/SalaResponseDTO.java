package com.syncspace.api.dto;

import com.syncspace.api.enums.TipoSala;
import com.syncspace.api.model.Sala;

public record SalaResponseDTO(Long id, String nome, Integer capacidade, TipoSala tipo) {
    public SalaResponseDTO(Sala sala) {
        this(sala.getId(), sala.getNome(), sala.getCapacidade(), sala.getTipo());
    }
}
