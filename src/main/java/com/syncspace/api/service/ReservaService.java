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
import com.syncspace.api.model.Medico;
import com.syncspace.api.model.Paciente;
import com.syncspace.api.model.Reserva;
import com.syncspace.api.model.Sala;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.MedicoRepository;
import com.syncspace.api.repository.PacienteRepository;
import com.syncspace.api.repository.ReservaRepository;
import com.syncspace.api.repository.SalaRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final PacienteRepository pacienteRepository;
    private final SalaRepository salaRepository;
    private final MedicoRepository medicoRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            PacienteRepository pacienteRepository,
            SalaRepository salaRepository,
            MedicoRepository medicoRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.pacienteRepository = pacienteRepository;
        this.salaRepository = salaRepository;
        this.medicoRepository = medicoRepository;
    }

    public List<ReservaResponseDTO> findMyConsultations() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Usuario usuario)
                || usuario.getRole() != UserRole.MEDICO) {
            throw new AccessDeniedException("A agenda é restrita ao médico autenticado.");
        }
        return reservaRepository.findAllByMedico_Usuario_IdOrderByDataHoraInicioAsc(usuario.getId())
                .stream()
                .map(ReservaResponseDTO::new)
                .toList();
    }

    @Transactional
    public ReservaResponseDTO create(ReservaRequestDTO request) {
        validarHorario(request);
        Paciente paciente = buscarPaciente(request.pacienteId());
        Sala sala = buscarSala(request.salaId());
        Medico medico = buscarMedico(request.medicoId());
        validarDisponibilidade(request, null);

        Reserva reserva = Reserva.builder()
                .paciente(paciente)
                .sala(sala)
                .medico(medico)
                .dataHoraInicio(request.dataHoraInicio())
                .dataHoraFim(request.dataHoraFim())
                .build();
        return new ReservaResponseDTO(reservaRepository.save(reserva));
    }

    @Transactional
    public ReservaResponseDTO update(Long id, ReservaRequestDTO request) {
        validarHorario(request);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ConsultaNaoEncontradaException("Consulta não encontrada."));
        validarDisponibilidade(request, id);

        reserva.setPaciente(buscarPaciente(request.pacienteId()));
        reserva.setSala(buscarSala(request.salaId()));
        reserva.setMedico(buscarMedico(request.medicoId()));
        reserva.setDataHoraInicio(request.dataHoraInicio());
        reserva.setDataHoraFim(request.dataHoraFim());
        return new ReservaResponseDTO(reserva);
    }

    private void validarHorario(ReservaRequestDTO request) {
        if (!request.dataHoraInicio().before(request.dataHoraFim())) {
            throw new ConsultaHorarioInvalidoException("O início deve ser anterior ao fim da consulta.");
        }
    }

    private void validarDisponibilidade(ReservaRequestDTO request, Long reservaId) {
        boolean conflito;
        if (reservaId == null) {
            conflito = reservaRepository.existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.salaId(), request.dataHoraFim(), request.dataHoraInicio()
            ) || reservaRepository.existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.medicoId(), request.dataHoraFim(), request.dataHoraInicio()
            ) || reservaRepository.existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
                    request.pacienteId(), request.dataHoraFim(), request.dataHoraInicio()
            );
        } else {
            conflito = reservaRepository.existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
                    request.salaId(), request.dataHoraFim(), request.dataHoraInicio(), reservaId
            ) || reservaRepository.existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
                    request.medicoId(), request.dataHoraFim(), request.dataHoraInicio(), reservaId
            ) || reservaRepository.existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
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
