package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private ReservaService reservaService;

    @Test
    void criarReserva_ComSucesso() {
        Usuario usuario = new Usuario();

        Sala sala = new Sala();
        sala.setCapacidadeInicial(10);

        ReservaRequestDTO reservaRequestDTO = new ReservaRequestDTO(
                null,
                "João da Silva",
                "Consulta médica",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1L,
                5
        );

        Reserva reservaEsperada = new Reserva();
        reservaEsperada.setNomeDoPaciente("João da Silva");

        Mockito.when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        Mockito.when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(false);
        Mockito.when(reservaRepository.save(Mockito.any(Reserva.class))).thenReturn(reservaEsperada);

        Reserva reserva = reservaService.criarReserva(usuario, reservaRequestDTO);

        Assertions.assertNotNull(reserva);
        Assertions.assertEquals("João da Silva", reserva.getNomeDoPaciente());
    }

    @Test
    void criarReserva_DeveLancarExcecao_QuandoCapacidadeExcedida() {
        Usuario usuario = new Usuario();

        Sala sala = new Sala();
        sala.setCapacidadeInicial(10);

        ReservaRequestDTO reservaRequestDTO = new ReservaRequestDTO(
                null,
                "João da Silva",
                "Consulta médica",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1L,
                15
        );

        Mockito.when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        Mockito.when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(false);

        Assertions.assertThrows(CapacidadeExcedidaException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });
    }

    @Test
    void criarReserva_DeveLancarExcecao_QuandoHorarioIndisponivel() {
        Usuario usuario = new Usuario();

        Sala sala = new Sala();
        sala.setCapacidadeInicial(10);

        ReservaRequestDTO reservaRequestDTO = new ReservaRequestDTO(
                null,
                "João da Silva",
                "Consulta médica",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1L,
                5
        );

        Mockito.when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        Mockito.when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(true);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });
    }

    @Test
    void criarReserva_DeveLancarExcecao_QuandoSalaNaoEncontrada() {
        Usuario usuario = new Usuario();

        ReservaRequestDTO reservaRequestDTO = new ReservaRequestDTO(
                null,
                "João da Silva",
                "Consulta médica",
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                1L,
                5
        );

        Mockito.when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });
    }
}