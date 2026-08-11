package com.br.syncspace.domain.sala;

import com.br.syncspace.domain.sala.dto.SalaRequestDTO;
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

    public Sala criarSala(SalaRequestDTO requestDTO) {
        Sala sala = new Sala();
        sala.setNome(requestDTO.nome());
        sala.setDescricao(requestDTO.descricao());
        sala.setCapacidade(requestDTO.capacidade());
        sala.setStatus(SalaStatus.ATIVA);
        return salaRepository.save(sala);
    }

    public Sala atualizarSala(SalaRequestDTO requestDTO, Long id) {
        Sala salaDoBanco = verSala(id);
        salaDoBanco.setNome(requestDTO.nome());
        salaDoBanco.setDescricao(requestDTO.descricao());
        salaDoBanco.setCapacidade(requestDTO.capacidade());
        return salaRepository.save(salaDoBanco);
    }

    public void deletarSala(Long id) {
        if (!salaRepository.existsById(id)) {
            throw new SalaNaoEncontradaException("Sala não encontrada");
        }
        salaRepository.deleteById(id);
    }
}