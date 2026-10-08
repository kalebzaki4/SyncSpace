package com.syncspace.api.service;

import com.syncspace.api.dto.MedicoRequestDTO;
import com.syncspace.api.dto.MedicoResponseDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Medico;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.MedicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {
    private final UsuarioService usuarioService;

    private final MedicoRepository medicoRepository;

    public MedicoService(UsuarioService usuarioService, MedicoRepository medicoRepository) {
        this.usuarioService = usuarioService;
        this.medicoRepository = medicoRepository;
    }

    public List<Medico> findAllMedicos() {
        return medicoRepository.findAll();
    }

    public Medico findById(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }

    @Transactional
    public MedicoResponseDTO createMedico(MedicoRequestDTO medicoRequestDTO) {
        Usuario usuario = usuarioService.createUsuario(medicoRequestDTO.usuario(), UserRole.MEDICO);

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setEspecialidade(medicoRequestDTO.especialidade());
        medico.setCrm(medicoRequestDTO.crm());

        Medico medicoSalvo = medicoRepository.save(medico);
        return new MedicoResponseDTO(medicoSalvo);
    }
}
