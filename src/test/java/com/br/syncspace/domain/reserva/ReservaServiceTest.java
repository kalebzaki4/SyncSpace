package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.HoraErradaException;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaEsperada);

        Reserva reserva = reservaService.criarReserva(usuario, reservaRequestDTO);

        assertNotNull(reserva);
        assertEquals("João da Silva", reserva.getNomeDoPaciente());
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

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(false);

        assertThrows(CapacidadeExcedidaException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
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

        when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim())).thenReturn(true);

        assertThrows(HoraErradaException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
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

        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SalaNaoEncontradaException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @ParameterizedTest(name = "Cenário de Data Inválida {index}")
    @MethodSource("fornecerDatasInvalidas")
    void criarReserva_DeveLancarExcecao_QuandoDatasForemInvalidas(LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = new Usuario();

        Sala sala = new Sala();
        sala.setCapacidadeInicial(10);

        ReservaRequestDTO reservaRequestDTO = new ReservaRequestDTO(
                null,
                "João da Silva",
                "Consulta médica",
                inicio,
                fim,
                1L,
                5
        );

        assertThrows(HoraErradaException.class, () -> {
            reservaService.criarReserva(usuario, reservaRequestDTO);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    private static Stream<Arguments> fornecerDatasInvalidas() {
        return Stream.of(
                Arguments.of(LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(1)), // Início maior que fim
                Arguments.of(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(1)), // Início igual ao fim (futuro)
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().minusHours(1)), // Início igual ao fim (passado)
                Arguments.of(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1)) // Início no passado
        );
    }

    @Test
    void criarReserva_DeveBuscarPorIdDaSala() {
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

        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(reservaRepository.existeReservaNoHorario(1L, reservaRequestDTO.dataHoraInicio(), reservaRequestDTO.dataHoraFim()))
                .thenReturn(false);

        reservaService.criarReserva(usuario, reservaRequestDTO);

        verify(salaRepository, times(1)).findById(1L);
    }
}