package com.syncspace.api.service;

import com.syncspace.api.dto.ReservaRequestDTO;
import com.syncspace.api.dto.ReservaResponseDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.exception.ConsultaHorarioInvalidoException;
import com.syncspace.api.exception.ConsultaIndisponivelException;
import com.syncspace.api.exception.ConsultaNaoEncontradaException;
import com.syncspace.api.exception.MedicoNaoEncontradoException;
import com.syncspace.api.exception.PacienteNaoEncontradoException;
import com.syncspace.api.exception.SalaNaoEncontradaException;
import com.syncspace.api.model.*;
import com.syncspace.api.repository.MedicoRepository;
import com.syncspace.api.repository.PacienteRepository;
import com.syncspace.api.repository.ConsultaRepository;
import com.syncspace.api.repository.SalaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ConsultaService {
    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final SalaRepository salaRepository;
    private final MedicoRepository medicoRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            PacienteRepository pacienteRepository,
            SalaRepository salaRepository,
            MedicoRepository medicoRepository
    ) {
        this.consultaRepository = consultaRepository;
        this.pacienteRepository = pacienteRepository;
        this.salaRepository = salaRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<ReservaResponseDTO> findMyConsultations(Usuario usuario) {
        if (usuario == null || usuario.getRole() != UserRole.MEDICO) {
            throw new AccessDeniedException("A agenda é restrita ao médico autenticado.");
        }
        return consultaRepository.findAllByMedico_Usuario_IdOrderByDataHoraInicioAsc(usuario.getId())
                .stream()
                .map(ReservaResponseDTO::new)
                .toList();
    }

    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO requestDTO) {
        validarHorario(requestDTO);
        Paciente paciente = buscarPaciente(requestDTO.pacienteId());
        Sala sala = buscarSala(requestDTO.salaId());
        Medico medico = buscarMedico(requestDTO.medicoId());
        validarDisponibilidade(requestDTO, null);

        Consulta consulta = Consulta.builder()
                .paciente(paciente)
                .sala(sala)
                .medico(medico)
                .dataHoraInicio(requestDTO.dataHoraInicio())
                .statusConsulta(requestDTO.statusConsulta())
                .dataHoraFim(requestDTO.dataHoraFim())
                .build();
        return new ReservaResponseDTO(consultaRepository.save(consulta));
    }

    @Transactional
    public ReservaResponseDTO update(Long id, ReservaRequestDTO request) {
        validarHorario(request);
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException("Consulta não encontrada."));
        validarDisponibilidade(request, id);

        consulta.setPaciente(buscarPaciente(request.pacienteId()));
        consulta.setSala(buscarSala(request.salaId()));
        consulta.setMedico(buscarMedico(request.medicoId()));
        consulta.setDataHoraInicio(request.dataHoraInicio());
        consulta.setDataHoraFim(request.dataHoraFim());
        return new ReservaResponseDTO(consulta);
    }

    @Transactional
    public void delete(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException("Consulta não encontrada."));
        consultaRepository.delete(consulta);
    }

    private void validarHorario(ReservaRequestDTO request) {
        if (!request.dataHoraInicio().before(request.dataHoraFim())) {
            throw new ConsultaHorarioInvalidoException("O início deve ser anterior ao fim da consulta.");
        }
    }

    private void validarDisponibilidade(ReservaRequestDTO request, Long reservaId) {
        boolean conflito;
        if (reservaId == null) {
            conflito = consultaRepository.existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.salaId(), request.dataHoraFim(), request.dataHoraInicio()
            ) || consultaRepository.existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.medicoId(), request.dataHoraFim(), request.dataHoraInicio()
            ) || consultaRepository.existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.pacienteId(), request.dataHoraFim(), request.dataHoraInicio()
            );
        } else {
            conflito = consultaRepository.existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
                    request.salaId(), request.dataHoraFim(), request.dataHoraInicio(), reservaId
            ) || consultaRepository.existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
                    request.medicoId(), request.dataHoraFim(), request.dataHoraInicio(), reservaId
            ) || consultaRepository.existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
                    request.pacienteId(), request.dataHoraFim(), request.dataHoraInicio(), reservaId
            );
        }
        if (conflito) {
            throw new ConsultaIndisponivelException("Paciente, médico ou sala indisponível nesse horário.");
        }
    }

    private Paciente buscarPaciente(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new PacienteNaoEncontradoException("Paciente não encontrado."));
    }

    private Sala buscarSala(Long id) {
        return salaRepository.findById(id)
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada."));
    }

    private Medico buscarMedico(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new MedicoNaoEncontradoException("Médico não encontrado."));
    }
}
