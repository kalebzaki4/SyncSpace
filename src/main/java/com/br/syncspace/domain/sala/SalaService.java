package com.br.syncspace.domain.sala;

import com.br.syncspace.infra.exception.SalaNaoCriadaException;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    public List<Sala> listarSalas() {
        return salaRepository.findAll();
    }

    public Sala verSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada"));
    }

    public Sala criarSala(Sala sala) {
        return salaRepository.save(sala);
    }

    public Sala atualizarSala(Sala sala) {
        if (!salaRepository.existsById(sala.getId())) {
            throw new SalaNaoEncontradaException("Sala não encontrada");
        }
        return salaRepository.save(sala);
    }

    public void deletarSala(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new SalaNaoEncontradaException("Sala não encontrada");
        }
        salaRepository.deleteById(id);
    }
}
