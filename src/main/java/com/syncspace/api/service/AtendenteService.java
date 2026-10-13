package com.syncspace.api.service;

import com.syncspace.api.dto.AtendenteRequestDTO;
import com.syncspace.api.dto.AtendenteResponseDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Atendente;
import com.syncspace.api.model.Medico;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.AtendenteRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendenteService {
    private final AtendenteRepository atendenteRepository;

    private final UsuarioService usuarioService;

    public AtendenteService(AtendenteRepository atendenteRepository, UsuarioService usuarioService) {
        this.atendenteRepository = atendenteRepository;
        this.usuarioService = usuarioService;
    }

    public List<Atendente> findAllAtendentes() {
        return atendenteRepository.findAll();
    }

    public Atendente findById(Long id) {
        return atendenteRepository.findById(id).orElseThrow(() -> new RuntimeException("Atendente não encontrado"));
    }

    @Transactional
    public AtendenteResponseDTO createAtendente(AtendenteRequestDTO atendenteRequestDTO) {
        Usuario usuario = usuarioService.createUsuario(atendenteRequestDTO.usuario(), UserRole.ATENDENTE);

        Atendente atendente = new Atendente();
        atendente.setUsuario(usuario);
        atendente.setSetor(atendenteRequestDTO.setor());

        atendenteRepository.save(atendente);
        return new AtendenteResponseDTO(atendente);

    }
}