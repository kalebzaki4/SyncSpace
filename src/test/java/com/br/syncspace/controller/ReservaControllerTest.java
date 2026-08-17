package com.br.syncspace.controller;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.domain.reserva.ReservaRepository;
import com.br.syncspace.domain.reserva.ReservaService;
import com.br.syncspace.domain.reserva.Status;
import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaRepository;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.infra.exception.ReservaNaoEncontradaException;
import com.br.syncspace.infra.security.SecurityFilter;
import com.br.syncspace.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = ReservaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityFilter.class
        )
)
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private ReservaService reservaService;

    @MockBean
    private ReservaRepository reservaRepository;

    @MockBean
    private SalaRepository salaRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;
    private Sala sala;
    private Reserva reserva;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");
        usuario.setNome("Usuário Teste");
        usuario.setRole(UserRole.USER);

        sala = new Sala();
        sala.setId(1L);
        sala.setNome("Sala 1");

        dataInicio = LocalDateTime.now().plusDays(1);
        dataFim = dataInicio.plusHours(2);

        reserva = new Reserva();
        reserva.setId(1L);
        reserva.setNomeDoPaciente("Paciente Teste");
        reserva.setDescricao("Descrição da reserva");
        reserva.setDataHoraInicio(dataInicio);
        reserva.setDataHoraFim(dataFim);
        reserva.setQuantidadePessoas(5);
        reserva.setStatus(Status.ATIVA);
        reserva.setUsuario(usuario);
        reserva.setSala(sala);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listarReservas_DeveRetornar200ComLista_QuandoAdmin() throws Exception {
        Reserva reserva2 = new Reserva();
        reserva2.setId(2L);
        reserva2.setNomeDoPaciente("Outro Paciente");
        reserva2.setUsuario(usuario);
        reserva2.setSala(sala);

        when(reservaService.listarReservas()).thenReturn(List.of(reserva, reserva2));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nomeDoPaciente").value("Paciente Teste"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nomeDoPaciente").value("Outro Paciente"));

        verify(reservaService, times(1)).listarReservas();
    }

    @Test
    @WithMockUser(roles = "USER")
    void listarReservas_DeveRetornar403_QuandoNaoForAdmin() throws Exception {
        mockMvc.perform(get("/reservas"))
                .andExpect(status().isForbidden());

        verify(reservaService, never()).listarReservas();
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void listarReservasUsuario_DeveRetornar200ComReservasDoUsuario() throws Exception {
        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuario));
        when(reservaService.listarReservasPorUsuario(1L)).thenReturn(List.of(reserva));

        mockMvc.perform(get("/reservas/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].usuarioId").value(1));

        verify(reservaService, times(1)).listarReservasPorUsuario(1L);
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void criarReserva_DeveRetornar201_QuandoDadosForemValidos() throws Exception {
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(
                null,
                "Paciente Teste",
                "Descrição",
                dataInicio,
                dataFim,
                1L,
                5
        );

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuario));
        when(reservaService.criarReserva(any(Usuario.class), any(ReservaRequestDTO.class))).thenReturn(reserva);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeDoPaciente").value("Paciente Teste"))
                .andExpect(jsonPath("$.salaId").value(1));

        verify(reservaService, times(1)).criarReserva(any(Usuario.class), any(ReservaRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void criarReserva_DeveRetornar400_QuandoDadosForemInvalidos() throws Exception {
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(
                null,
                "",
                "",
                null,
                null,
                null,
                null
        );

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(reservaService, never()).criarReserva(any(), any());
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void atualizarReserva_DeveRetornar200_QuandoDadosForemValidos() throws Exception {
        ReservaRequestDTO requestDTO = new ReservaRequestDTO(
                1L,
                "Paciente Atualizado",
                "Nova descrição",
                dataInicio,
                dataFim,
                1L,
                8
        );

        Reserva reservaAtualizada = new Reserva();
        reservaAtualizada.setId(1L);
        reservaAtualizada.setNomeDoPaciente("Paciente Atualizado");
        reservaAtualizada.setDescricao("Nova descrição");
        reservaAtualizada.setDataHoraInicio(dataInicio);
        reservaAtualizada.setDataHoraFim(dataFim);
        reservaAtualizada.setQuantidadePessoas(8);
        reservaAtualizada.setStatus(Status.ATIVA);
        reservaAtualizada.setUsuario(usuario);
        reservaAtualizada.setSala(sala);

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuario));
        when(reservaService.atualizarReserva(any(Usuario.class), any(ReservaRequestDTO.class))).thenReturn(reservaAtualizada);

        mockMvc.perform(put("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nomeDoPaciente").value("Paciente Atualizado"))
                .andExpect(jsonPath("$.quantidadePessoas").value(8));

        verify(reservaService, times(1)).atualizarReserva(any(Usuario.class), any(ReservaRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "outro@email.com", roles = "USER")
    void atualizarReserva_DeveRetornar403_QuandoUsuarioNaoForDono() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("outro@email.com");
        outroUsuario.setRole(UserRole.USER);

        ReservaRequestDTO requestDTO = new ReservaRequestDTO(
                1L,
                "Paciente",
                "Descrição",
                dataInicio,
                dataFim,
                1L,
                5
        );

        when(usuarioRepository.findByEmail("outro@email.com")).thenReturn(java.util.Optional.of(outroUsuario));
        when(reservaService.atualizarReserva(any(Usuario.class), any(ReservaRequestDTO.class)))
                .thenThrow(new AccessDeniedException("Você não tem permissão para atualizar esta reserva."));

        mockMvc.perform(put("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isForbidden());

        verify(reservaService, times(1)).atualizarReserva(any(Usuario.class), any(ReservaRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void deletarReserva_DeveRetornar204_QuandoUsuarioForDono() throws Exception {
        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuario));
        doNothing().when(reservaService).deletarReserva(any(Usuario.class), eq(1L));

        mockMvc.perform(delete("/reservas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(reservaService, times(1)).deletarReserva(any(Usuario.class), eq(1L));
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void deletarReserva_DeveRetornar404_QuandoReservaNaoExistir() throws Exception {
        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuario));
        doThrow(new ReservaNaoEncontradaException("Reserva não encontrada."))
                .when(reservaService).deletarReserva(any(Usuario.class), eq(99L));

        mockMvc.perform(delete("/reservas/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(reservaService, times(1)).deletarReserva(any(Usuario.class), eq(99L));
    }

    @Test
    @WithMockUser(username = "outro@email.com", roles = "USER")
    void deletarReserva_DeveRetornar403_QuandoUsuarioNaoForDono() throws Exception {
        Usuario outroUsuario = new Usuario();
        outroUsuario.setId(2L);
        outroUsuario.setEmail("outro@email.com");
        outroUsuario.setRole(UserRole.USER);

        when(usuarioRepository.findByEmail("outro@email.com")).thenReturn(java.util.Optional.of(outroUsuario));
        doThrow(new AccessDeniedException("Você não tem permissão para deletar esta reserva."))
                .when(reservaService).deletarReserva(any(Usuario.class), eq(1L));

        mockMvc.perform(delete("/reservas/{id}", 1L))
                .andExpect(status().isForbidden());

        verify(reservaService, times(1)).deletarReserva(any(Usuario.class), eq(1L));
    }
}
