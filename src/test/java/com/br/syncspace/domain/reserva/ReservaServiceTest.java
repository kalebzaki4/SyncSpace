package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.HoraErradaException;
import com.br.syncspace.infra.exception.ReservaNaoEncontradaException;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    void listarReservas_DeveRetornarListaDeReservas() {
        Reserva reserva1 = new Reserva();
        reserva1.setId(1L);
        Reserva reserva2 = new Reserva();
        reserva2.setId(2L);

        when(reservaRepository.findAll()).thenReturn(List.of(reserva1, reserva2));

        List<Reserva> resultado = reservaService.listarReservas();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    void listarReservasPorUsuario_DeveRetornarReservasDoUsuario() {
        Long usuarioId = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(1L);

        when(reservaRepository.findByUsuarioId(usuarioId)).thenReturn(List.of(reserva));

        List<Reserva> resultado = reservaService.listarReservasPorUsuario(usuarioId);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository, times(1)).findByUsuarioId(usuarioId);
    }

    @Test
    void atualizarReserva_DeveAtualizarComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Sala sala = new Sala();
        sala.setId(1L);
        sala.setCapacidadeInicial(10);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);
        reservaExistente.setSala(sala);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Nova descrição",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                1L,
                5
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.existeReservaNoHorarioExcluindoReserva(1L, 1L, dto.dataHoraInicio(), dto.dataHoraFim())).thenReturn(false);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reservaExistente);

        Reserva resultado = reservaService.atualizarReserva(usuario, dto);

        assertNotNull(resultado);
        assertEquals("Maria Silva", resultado.getNomeDoPaciente());
        assertEquals("Nova descrição", resultado.getDescricao());
        verify(reservaRepository, times(1)).save(reservaExistente);
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoIdForNulo() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                null,
                "Maria Silva",
                "Descrição",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                1L,
                5
        );

        assertThrows(IllegalArgumentException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).findById(any());
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoReservaNaoEncontrada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                99L,
                "Maria Silva",
                "Descrição",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                1L,
                5
        );

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNaoEncontradaException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoUsuarioNaoForDono() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(2L);

        Usuario donoDaReserva = new Usuario();
        donoDaReserva.setId(1L);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(donoDaReserva);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Descrição",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(2),
                1L,
                5
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));

        assertThrows(AccessDeniedException.class, () -> reservaService.atualizarReserva(usuarioLogado, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @ParameterizedTest(name = "Cenário de Data Inválida na Atualização {index}")
    @MethodSource("fornecerDatasInvalidas")
    void atualizarReserva_DeveLancarExcecao_QuandoDatasForemInvalidas(LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Descrição",
                inicio,
                fim,
                1L,
                5
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));

        assertThrows(HoraErradaException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoHorarioIndisponivel() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Sala sala = new Sala();
        sala.setCapacidadeInicial(10);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);
        reservaExistente.setSala(sala);

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Descrição",
                inicio,
                fim,
                1L,
                5
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.existeReservaNoHorarioExcluindoReserva(1L, 1L, inicio, fim)).thenReturn(true);

        assertThrows(HoraErradaException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoSalaNaoEncontrada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Descrição",
                inicio,
                fim,
                99L,
                5
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.existeReservaNoHorarioExcluindoReserva(99L, 1L, inicio, fim)).thenReturn(false);
        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SalaNaoEncontradaException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void atualizarReserva_DeveLancarExcecao_QuandoCapacidadeExcedida() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Sala sala = new Sala();
        sala.setCapacidadeInicial(5);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);
        reservaExistente.setSala(sala);

        LocalDateTime inicio = LocalDateTime.now().plusDays(1);
        LocalDateTime fim = inicio.plusHours(2);

        ReservaRequestDTO dto = new ReservaRequestDTO(
                1L,
                "Maria Silva",
                "Descrição",
                inicio,
                fim,
                1L,
                10
        );

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));
        when(reservaRepository.existeReservaNoHorarioExcluindoReserva(1L, 1L, inicio, fim)).thenReturn(false);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        assertThrows(CapacidadeExcedidaException.class, () -> reservaService.atualizarReserva(usuario, dto));
        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    void deletarReserva_DeveDeletarComSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(usuario);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));

        reservaService.deletarReserva(usuario, 1L);

        verify(reservaRepository, times(1)).findById(1L);
        verify(reservaRepository, times(1)).delete(reservaExistente);
    }

    @Test
    void deletarReserva_DeveLancarExcecao_QuandoReservaNaoEncontrada() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ReservaNaoEncontradaException.class, () -> reservaService.deletarReserva(usuario, 99L));
        verify(reservaRepository, never()).delete(any(Reserva.class));
    }

    @Test
    void deletarReserva_DeveLancarExcecao_QuandoUsuarioNaoForDono() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(2L);

        Usuario donoDaReserva = new Usuario();
        donoDaReserva.setId(1L);

        Reserva reservaExistente = new Reserva();
        reservaExistente.setId(1L);
        reservaExistente.setUsuario(donoDaReserva);

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reservaExistente));

        assertThrows(AccessDeniedException.class, () -> reservaService.deletarReserva(usuarioLogado, 1L));
        verify(reservaRepository, never()).delete(any(Reserva.class));
    }
}