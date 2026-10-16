package com.syncspace.api.service;

import com.syncspace.api.dto.PacienteResponseDTO;
import com.syncspace.api.dto.PacienteRequestDTO;
import com.syncspace.api.exception.PacienteComConsultasException;
import com.syncspace.api.exception.PacienteJaExisteException;
import com.syncspace.api.exception.PacienteNaoEncontradoException;
import com.syncspace.api.model.Paciente;
import com.syncspace.api.repository.PacienteRepository;
import com.syncspace.api.repository.ReservaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PacienteService {
    public static final String PACIENTE_NAO_ENCONTRADO = "Paciente não encontrado";
    private final PacienteRepository pacienteRepository;
    private final ReservaRepository reservaRepository;

    public PacienteService(PacienteRepository pacienteRepository, ReservaRepository reservaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<PacienteResponseDTO> findAllPacientes() {
        return pacienteRepository.findAll().stream().map(PacienteResponseDTO::new).toList();
    }

    public PacienteResponseDTO findById(Long id) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(() -> new PacienteNaoEncontradoException(PACIENTE_NAO_ENCONTRADO));
        return new PacienteResponseDTO(paciente);
    }

    @Transactional
    public PacienteResponseDTO createPaciente(PacienteRequestDTO pacienteRequestDTO) {
        if (pacienteRepository.existsByProntuario(pacienteRequestDTO.prontuario())) {
            throw new PacienteJaExisteException("Já existe um paciente cadastrado com este prontuário.");
        }

        Paciente paciente = Paciente.builder().alergias(pacienteRequestDTO.alergias()).contatoDeEmergencia(pacienteRequestDTO.contatoDeEmergencia()).tipoSanguineo(pacienteRequestDTO.tipoSanguineo()).doencasCronicas(pacienteRequestDTO.doencasCronicas()).medicamentos(pacienteRequestDTO.medicamentos()).telefoneContatoEmergencia(pacienteRequestDTO.telefoneContatoEmergencia()).prontuario(pacienteRequestDTO.prontuario()).build();

        Paciente pacienteSalvo = pacienteRepository.save(paciente);
        return new PacienteResponseDTO(pacienteSalvo);
    }

    @Transactional
    public PacienteResponseDTO updatePaciente(Long id, PacienteRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(id).orElseThrow(() -> new PacienteNaoEncontradoException(PACIENTE_NAO_ENCONTRADO));
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
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException(PACIENTE_NAO_ENCONTRADO));
        if (reservaRepository.existsByPaciente_Id(id)) {
            throw new PacienteComConsultasException("Não é possível remover um paciente com consultas cadastradas.");
        }
        pacienteRepository.delete(paciente);
    }
}
