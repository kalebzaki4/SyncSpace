package com.syncspace.api.service;

import com.syncspace.api.dto.DadosSala;
import com.syncspace.api.model.Sala;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.SalaRepository;
import com.syncspace.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaService {

    private SalaRepository salaRepository;

    @Autowired
    public SalaService(SalaRepository salaRepository, UsuarioRepository usuarioRepository) {
        this.salaRepository = salaRepository;
    }

    // ver sala expecifica
    @Transactional
    public Sala findSalaById(Long sala) {
        return salaRepository.findById(sala).orElseThrow(() -> new RuntimeException("Sala não encontrada"));
    }

    // ver todas as salas
    public List<Sala> findAllSalas() {
        return salaRepository.findAll();
    }

    // criar sala
    @Transactional
    public Sala createSala(Sala sala) {
        Optional<Sala> optionalSala = salaRepository.findByNome(sala.getNome());
        if (optionalSala.isPresent()) {
            throw new RuntimeException("Sala já cadastrada");
        }
        return salaRepository.save(sala);
    }

    public Sala updateSala(Long id, DadosSala dadosAtualizacao) {
    }

    public void deleteSala(Long id) {
    }
}
