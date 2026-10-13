package com.syncspace.api.service;

import com.syncspace.api.dto.PacienteResponseDTO;
import com.syncspace.api.dto.PacienteRequestDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Paciente;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {
    private final UsuarioService usuarioService;

    private final PacienteRepository pacienteRepository;

    public PacienteService(UsuarioService usuarioService, PacienteRepository pacienteRepository) {
        this.usuarioService = usuarioService;
        this.pacienteRepository = pacienteRepository;
    }

    public List<Paciente> findAllPacientes() {
        return pacienteRepository.findAll();
    }

    public Paciente findById(Long id) {
        return pacienteRepository.findById(id).orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    @Transactional
    public PacienteResponseDTO createPaciente(PacienteRequestDTO pacienteRequestDTO) {
        Usuario usuario = usuarioService.createUsuario(pacienteRequestDTO.usuario(), UserRole.PACIENTE);

        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setTipoSanguineo(pacienteRequestDTO.tipoSanguineo());
        paciente.setAlergias(pacienteRequestDTO.alergias());
        paciente.setDoencasCronicas(pacienteRequestDTO.doencasCronicas());
        paciente.setMedicamentos(pacienteRequestDTO.medicamentos());
        paciente.setContatoDeEmergencia(pacienteRequestDTO.contatoDeEmergencia());
        paciente.setTelefoneContatoEmergencia(pacienteRequestDTO.telefoneContatoEmergencia());
        paciente.setProntuario(pacienteRequestDTO.prontuario());

        pacienteRepository.save(paciente);
        return new PacienteResponseDTO(paciente);
    }
}
