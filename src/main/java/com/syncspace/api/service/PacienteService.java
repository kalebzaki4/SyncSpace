package com.syncspace.api.service;

import com.syncspace.api.dto.PacienteResponseDTO;
import com.syncspace.api.dto.PacienteRequestDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.exception.PacienteJaExisteException;
import com.syncspace.api.exception.PacienteNaoEncontradoException;
import com.syncspace.api.model.Paciente;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.PacienteRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public List<PacienteResponseDTO> findAllPacientes() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN
                && usuario.getRole() != UserRole.ATENDENTE
                && usuario.getRole() != UserRole.MEDICO) {
            throw new AccessDeniedException("Acesso negado à lista de pacientes.");
        }
        return pacienteRepository.findAll().stream().map(PacienteResponseDTO::new).toList();
    }

    public PacienteResponseDTO findById(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado"));
        if (usuario.getRole() != UserRole.ADMIN
                && usuario.getRole() != UserRole.ATENDENTE
                && usuario.getRole() != UserRole.MEDICO
                && !paciente.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Acesso negado a este paciente.");
        }
        return new PacienteResponseDTO(paciente);
    }

    @Transactional
    public PacienteResponseDTO createPaciente(PacienteRequestDTO pacienteRequestDTO) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.PACIENTE && usuario.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Somente pacientes podem cadastrar o próprio perfil.");
        }

        if (pacienteRepository.existsByProntuario(pacienteRequestDTO.prontuario())) {
            throw new PacienteJaExisteException("Já existe um paciente cadastrado com este prontuário.");
        }

        if (pacienteRepository.existsByUsuario_Id(usuario.getId())) {
            throw new PacienteJaExisteException("O usuário autenticado já possui um cadastro de paciente.");
        }

        Paciente paciente = Paciente.builder()
                .usuario(usuario)
                .alergias(pacienteRequestDTO.alergias())
                .contatoDeEmergencia(pacienteRequestDTO.contatoDeEmergencia())
                .tipoSanguineo(pacienteRequestDTO.tipoSanguineo())
                .doencasCronicas(pacienteRequestDTO.doencasCronicas())
                .medicamentos(pacienteRequestDTO.medicamentos())
                .telefoneContatoEmergencia(pacienteRequestDTO.telefoneContatoEmergencia())
                .prontuario(pacienteRequestDTO.prontuario())
                .build();

        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        return new PacienteResponseDTO(pacienteSalvo);
    }

    @Transactional
    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO request) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado"));
        if (usuario.getRole() != UserRole.ADMIN
                && !paciente.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o próprio paciente ou um administrador pode atualizar este perfil.");
        }
        if (pacienteRepository.existsByProntuarioAndIdNot(request.prontuario(), id)) {
            throw new PacienteJaExisteException("Já existe um paciente cadastrado com este prontuário.");
        }

        paciente.setTipoSanguineo(request.tipoSanguineo());
        paciente.setAlergias(request.alergias());
        paciente.setDoencasCronicas(request.doencasCronicas());
        paciente.setMedicamentos(request.medicamentos());
        paciente.setContatoDeEmergencia(request.contatoDeEmergencia());
        paciente.setTelefoneContatoEmergencia(request.telefoneContatoEmergencia());
        paciente.setProntuario(request.prontuario());
        return new PacienteResponseDTO(paciente);
    }

    @Transactional
    public void deletePaciente(Long id) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Somente um administrador pode remover pacientes.");
        }
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado"));
        pacienteRepository.delete(paciente);
    }
}
