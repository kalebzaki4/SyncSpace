package com.syncspace.api.service;

import com.syncspace.api.dto.MedicoRequestDTO;
import com.syncspace.api.dto.MedicoResponseDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.exception.MedicoJaExisteExeption;
import com.syncspace.api.exception.MedicoNaoEncontradoException;
import com.syncspace.api.model.Medico;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.MedicoRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicoService {
    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    public List<MedicoResponseDTO> findAllMedicos() {
        return medicoRepository.findAll().stream().map(MedicoResponseDTO::new).toList();
    }

    public MedicoResponseDTO findById(Long id) {
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNaoEncontradoException("Médico não encontrado"));
        return new MedicoResponseDTO(medico);
    }

    @Transactional
    public MedicoResponseDTO createMedico(MedicoRequestDTO medicoRequestDTO) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.MEDICO && usuario.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Somente médicos podem cadastrar o próprio perfil.");
        }

        if (medicoRepository.existsByCrm(medicoRequestDTO.crm())) {
            throw new MedicoJaExisteExeption("Já existe um médico cadastrado com este CRM.");
        }

        if (medicoRepository.existsByUsuario_Id(usuario.getId())) {
            throw new MedicoJaExisteExeption("O usuário autenticado já possui um cadastro de médico.");
        }

        Medico medico = Medico.builder()
                .usuario(usuario)
                .crm(medicoRequestDTO.crm())
                .especialidade(medicoRequestDTO.especialidade())
                .build();

        Medico medicoSalvo = medicoRepository.save(medico);
        return new MedicoResponseDTO(medicoSalvo);
    }

    @Transactional
    public MedicoResponseDTO updateMedico(Long id, MedicoRequestDTO request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNaoEncontradoException("Médico não encontrado"));
        if (usuario.getRole() != UserRole.ADMIN
                && !medico.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o próprio médico ou um administrador pode atualizar este perfil.");
        }
        if (medicoRepository.existsByCrmAndIdNot(request.crm(), id)) {
            throw new MedicoJaExisteExeption("Já existe um médico cadastrado com este CRM.");
        }

        medico.setCrm(request.crm());
        medico.setEspecialidade(request.especialidade());
        return new MedicoResponseDTO(medico);
    }

    @Transactional
    public void deleteMedico(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Somente um administrador pode remover médicos.");
        }
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNaoEncontradoException("Médico não encontrado"));
        medicoRepository.delete(medico);
    }
}
