package com.br.syncspace.controller;

import com.br.syncspace.domain.sala.*;
import com.br.syncspace.domain.sala.dto.SalaRequestDTO;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import com.br.syncspace.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SalaController.class)
@AutoConfigureMockMvc(addFilters = false)
class SalaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalaService salaService;

    @MockBean
    private SalaRepository salaRepository;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    @WithMockUser(roles = "USER")
    void listarSalas_DeveRetornar200ComListaDeSalas() throws Exception {
        Sala sala1 = new Sala();
        sala1.setId(1L);
        sala1.setNome("Sala 1");
        sala1.setDescricao("Descrição 1");
        sala1.setCapacidadeInicial(10);
        sala1.setStatus(SalaStatus.ATIVA);

        Sala sala2 = new Sala();
        sala2.setId(2L);
        sala2.setNome("Sala 2");
        sala2.setCapacidadeInicial(20);
        sala2.setStatus(SalaStatus.ATIVA);

        when(salaService.listarSalas()).thenReturn(List.of(sala1, sala2));

        mockMvc.perform(get("/salas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Sala 1"))
                .andExpect(jsonPath("$[1].nome").value("Sala 2"));

        verify(salaService, times(1)).listarSalas();
    }

    @Test
    @WithMockUser(roles = "USER")
    void verSala_DeveRetornar200_QuandoIdExistir() throws Exception {
        Sala sala = new Sala();
        sala.setId(1L);
        sala.setNome("Sala A");
        sala.setDescricao("Sala de reunião");
        sala.setCapacidadeInicial(15);
        sala.setStatus(SalaStatus.ATIVA);

        when(salaService.verSala(1L)).thenReturn(sala);

        mockMvc.perform(get("/salas/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Sala A"))
                .andExpect(jsonPath("$.descricao").value("Sala de reunião"))
                .andExpect(jsonPath("$.capacidadeInicial").value(15));

        verify(salaService, times(1)).verSala(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void verSala_DeveRetornar404_QuandoIdNaoExistir() throws Exception {
        when(salaService.verSala(99L)).thenThrow(new SalaNaoEncontradaException("Sala não encontrada"));

        mockMvc.perform(get("/salas/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(salaService, times(1)).verSala(99L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void criarSala_DeveRetornar201_QuandoDadosForemValidos() throws Exception {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Nova", "Nova sala", 12);

        Sala salaCriada = new Sala();
        salaCriada.setId(1L);
        salaCriada.setNome("Sala Nova");
        salaCriada.setDescricao("Nova sala");
        salaCriada.setCapacidadeInicial(12);
        salaCriada.setStatus(SalaStatus.ATIVA);

        when(salaService.criarSala(any(SalaRequestDTO.class))).thenReturn(salaCriada);

        mockMvc.perform(post("/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/salas/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Sala Nova"))
                .andExpect(jsonPath("$.capacidadeInicial").value(12));

        verify(salaService, times(1)).criarSala(any(SalaRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void criarSala_DeveRetornar200_QuandoUsuarioAutenticado() throws Exception {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Nova", "Nova sala", 12);
        Sala salaCriada = new Sala();
        salaCriada.setId(1L);
        salaCriada.setNome("Sala Nova");
        salaCriada.setDescricao("Nova sala");
        salaCriada.setCapacidadeInicial(12);
        salaCriada.setStatus(SalaStatus.ATIVA);

        when(salaService.criarSala(any(SalaRequestDTO.class))).thenReturn(salaCriada);

        mockMvc.perform(post("/salas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());

        verify(salaService, times(1)).criarSala(any(SalaRequestDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void atualizarSala_DeveRetornar200_QuandoDadosForemValidos() throws Exception {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Atualizada", "Nova descrição", 20);

        Sala salaAtualizada = new Sala();
        salaAtualizada.setId(1L);
        salaAtualizada.setNome("Sala Atualizada");
        salaAtualizada.setDescricao("Nova descrição");
        salaAtualizada.setCapacidadeInicial(20);
        salaAtualizada.setStatus(SalaStatus.ATIVA);

        when(salaService.atualizarSala(any(SalaRequestDTO.class), eq(1L))).thenReturn(salaAtualizada);

        mockMvc.perform(put("/salas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Sala Atualizada"))
                .andExpect(jsonPath("$.descricao").value("Nova descrição"))
                .andExpect(jsonPath("$.capacidadeInicial").value(20));

        verify(salaService, times(1)).atualizarSala(any(SalaRequestDTO.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "USER")
    void atualizarSala_DeveRetornar200_QuandoUsuarioAutenticado() throws Exception {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Atualizada", "Descrição", 20);
        Sala salaAtualizada = new Sala();
        salaAtualizada.setId(1L);
        salaAtualizada.setNome("Sala Atualizada");
        salaAtualizada.setDescricao("Descrição");
        salaAtualizada.setCapacidadeInicial(20);
        salaAtualizada.setStatus(SalaStatus.ATIVA);

        when(salaService.atualizarSala(any(SalaRequestDTO.class), eq(1L))).thenReturn(salaAtualizada);

        mockMvc.perform(put("/salas/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());

        verify(salaService, times(1)).atualizarSala(any(SalaRequestDTO.class), eq(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarSala_DeveRetornar204_QuandoIdExistir() throws Exception {
        doNothing().when(salaService).deletarSala(1L);

        mockMvc.perform(delete("/salas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(salaService, times(1)).deletarSala(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void deletarSala_DeveRetornar204_QuandoUsuarioAutenticado() throws Exception {
        doNothing().when(salaService).deletarSala(1L);

        mockMvc.perform(delete("/salas/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(salaService, times(1)).deletarSala(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletarSala_DeveRetornar404_QuandoIdNaoExistir() throws Exception {
        doThrow(new SalaNaoEncontradaException("Sala não encontrada"))
                .when(salaService).deletarSala(99L);

        mockMvc.perform(delete("/salas/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(salaService, times(1)).deletarSala(99L);
    }
}
