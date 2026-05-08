package com.syncspace.api.service;

import com.syncspace.api.dto.DadosCriacaoSala;
import com.syncspace.api.model.Sala;
import com.syncspace.api.repository.SalaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private SalaRepository salaRepository;

    @Autowired
    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    // ver sala expecifica
    public Sala findSalaById(Long sala) {
        return salaRepository.findById(sala)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada"));
    }

    // ver todas as salas
    public List<Sala> findAllSalas() {
        return salaRepository.findAll();
    }

    // criar sala
    @Transactional
    public Sala createSala(@Valid DadosCriacaoSala dadosCadastro) {
        Optional<Sala> salaExistente = salaRepository.findByNome(dadosCadastro.nome());
        if (salaExistente.isPresent()) {
            throw new RuntimeException("Já existe uma sala com esse nome");
        }
        Sala novaSala = new Sala();
        novaSala.setNome(dadosCadastro.nome());
        novaSala.setDescricao(dadosCadastro.descricao());
        novaSala.setQuantidade(dadosCadastro.quantidade());
        LocalDateTime now = LocalDateTime.now();
        novaSala.setDataCriacao(now);
        novaSala.setTempoExpiracao(now.plusMinutes(dadosCadastro.tempoExpiracaoMinutos()));
        return salaRepository.save(novaSala);
    }

    // atualizar sala
    @Transactional
    public Sala updateSala(Long id, DadosCriacaoSala dadosAtualizacao) {
        Sala sala = (salaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sala não encontrada")));
        sala.setNome(dadosAtualizacao.nome());
        sala.setDescricao(dadosAtualizacao.descricao());
        sala.setQuantidade(dadosAtualizacao.quantidade());
        LocalDateTime now = LocalDateTime.now();
        sala.setTempoExpiracao(now.plusMinutes(dadosAtualizacao.tempoExpiracaoMinutos()));
        return salaRepository.save(sala);
    }

    // deletar sala
    @Transactional
    public void deleteSala(Long sala) {
        Sala salaDeletada = salaRepository.findById(sala).orElseThrow(() -> new RuntimeException("Sala não encontrada"));
        salaRepository.delete(salaDeletada);
    }

    // excluir salas expiradas
    @Transactional
    @Scheduled(fixedRate = 60000)
    public void scheduled() {
        salaRepository.deleteByTempoExpiracaoBefore(LocalDateTime.now());
    }
}
