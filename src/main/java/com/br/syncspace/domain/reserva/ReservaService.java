package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.HoraErradaException;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
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

    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

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

        Reserva novaReserva = new Reserva();
        novaReserva.setNomeDoPaciente(dto.nomeDoPaciente());
        novaReserva.setDescricao(dto.descricao());
        novaReserva.setDataHoraInicio(dto.dataHoraInicio());
        novaReserva.setDataHoraFim(dto.dataHoraFim());
        novaReserva.setSala(sala);
        novaReserva.setUsuario(usuario);

        return reservaRepository.save(novaReserva);
    }
}