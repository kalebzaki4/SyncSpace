package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.HoraErradaException;
import com.br.syncspace.infra.exception.ReservaNaoEncontradaException;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Reserva criarReserva(Usuario usuario, ReservaRequestDTO dto) {
        if (!dto.dataHoraFim().isAfter(dto.dataHoraInicio())) {
            throw new HoraErradaException("A data/hora de fim deve ser posterior à data/hora de início.");
        }

        boolean salaOcupada = reservaRepository.existeReservaNoHorario(
                dto.salaId(),
                dto.dataHoraInicio(),
                dto.dataHoraFim()
        );
        if (salaOcupada) {
            throw new HoraErradaException("A sala informada já possui uma reserva no horário selecionado.");
        }

        Sala sala = salaRepository.findById(dto.salaId())
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada."));

        if (dto.quantidadePessoas() > sala.getCapacidadeInicial()) {
            throw new CapacidadeExcedidaException("A quantidade de pessoas excede a capacidade da sala.");
        }

        Reserva novaReserva = new Reserva();
        novaReserva.setNomeDoPaciente(dto.nomeDoPaciente());
        novaReserva.setDescricao(dto.descricao());
        novaReserva.setDataHoraInicio(dto.dataHoraInicio());
        novaReserva.setDataHoraFim(dto.dataHoraFim());
        novaReserva.setQuantidadePessoas(dto.quantidadePessoas());
        novaReserva.setSala(sala);
        novaReserva.setUsuario(usuario);

        return reservaRepository.save(novaReserva);
    }

    @Transactional
    public Reserva atualizarReserva(Usuario usuario, ReservaRequestDTO reservaRequestDTO) {
        if (reservaRequestDTO.id() == null) {
            throw new IllegalArgumentException("O ID da reserva é obrigatório para atualização.");
        }

        Reserva reservaExistente = reservaRepository.findById(reservaRequestDTO.id())
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada."));

        if (!reservaExistente.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para atualizar esta reserva.");
        }

        if (!reservaRequestDTO.dataHoraFim().isAfter(reservaRequestDTO.dataHoraInicio())) {
            throw new HoraErradaException("A data/hora de fim deve ser posterior à data/hora de início.");
        }

        boolean salaOcupada = reservaRepository.existeReservaNoHorarioExcluindoReserva(
                reservaRequestDTO.salaId(),
                reservaRequestDTO.id(),
                reservaRequestDTO.dataHoraInicio(),
                reservaRequestDTO.dataHoraFim()
        );
        if (salaOcupada) {
            throw new HoraErradaException("A sala informada já possui uma reserva no horário selecionado.");
        }

        Sala sala = salaRepository.findById(reservaRequestDTO.salaId())
                .orElseThrow(() -> new SalaNaoEncontradaException("Sala não encontrada."));

        if (reservaRequestDTO.quantidadePessoas() > sala.getCapacidadeInicial()) {
            throw new CapacidadeExcedidaException("A quantidade de pessoas excede a capacidade da sala.");
        }

        reservaExistente.setNomeDoPaciente(reservaRequestDTO.nomeDoPaciente());
        reservaExistente.setDescricao(reservaRequestDTO.descricao());
        reservaExistente.setDataHoraInicio(reservaRequestDTO.dataHoraInicio());
        reservaExistente.setDataHoraFim(reservaRequestDTO.dataHoraFim());
        reservaExistente.setQuantidadePessoas(reservaRequestDTO.quantidadePessoas());
        reservaExistente.setSala(sala);

        return reservaRepository.save(reservaExistente);
    }

    @Transactional
    public void deletarReserva(Usuario usuario, Long reservaId) {
        Reserva reservaExistente = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ReservaNaoEncontradaException("Reserva não encontrada."));

        if (!reservaExistente.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Você não tem permissão para deletar esta reserva.");
        }

        reservaRepository.delete(reservaExistente);
    }
}