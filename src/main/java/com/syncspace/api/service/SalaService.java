package com.syncspace.api.service;

import com.syncspace.api.dto.SalaRequestDTO;
import com.syncspace.api.dto.SalaResponseDTO;
import com.syncspace.api.exception.SalaComConsultasException;
import com.syncspace.api.exception.SalaNaoEncontradaException;
import com.syncspace.api.model.Sala;
import com.syncspace.api.repository.ReservaRepository;
import com.syncspace.api.repository.SalaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SalaService {
    private final SalaRepository salaRepository;
    private final ReservaRepository reservaRepository;

    public SalaService(SalaRepository salaRepository, ReservaRepository reservaRepository) {
        this.salaRepository = salaRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<SalaResponseDTO> findAll() {
        return salaRepository.findAll().stream().map(SalaResponseDTO::new).toList();
    }

    @Transactional
    public SalaResponseDTO create(SalaRequestDTO request) {
        Sala sala = Sala.builder().nome(request.nome()).capacidade(request.capacidade()).tipo(request.tipo()).build();
        return new SalaResponseDTO(salaRepository.save(sala));
    }

    @Transactional
    public SalaResponseDTO update(Long id, SalaRequestDTO request) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada."));
        sala.setNome(request.nome());
        sala.setCapacidade(request.capacidade());
        sala.setTipo(request.tipo());
        return new SalaResponseDTO(sala);
    }

    @Transactional
    public void delete(Long id) {
        Sala sala = salaRepository.findById(id)
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada."));
        if (reservaRepository.existsBySala_Id(id)) {
            throw new SalaComConsultasException("Não é possível remover uma sala com consultas agendadas.");
        }
        salaRepository.delete(sala);
    }
}
