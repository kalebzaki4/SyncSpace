package com.br.syncspace.controller;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.domain.usuario.UsuarioService;
import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.UsuarioNaoEncontradoException;
import com.br.syncspace.infra.security.SecurityFilter;
import com.br.syncspace.infra.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = UsuarioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityFilter.class
        )
)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private TokenService tokenService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsuarios_DeveRetornar200ComLista_QuandoAdmin() throws Exception {
        Usuario usuario1 = new Usuario();
        usuario1.setId(1L);
        usuario1.setEmail("usuario1@email.com");
        usuario1.setNome("Usuário 1");
        usuario1.setRole(UserRole.USER);

        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("admin@email.com");
        usuario2.setNome("Admin");
        usuario2.setRole(UserRole.ADMIN);

        when(usuarioService.getAllUsuarios()).thenReturn(List.of(usuario1, usuario2));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("usuario1@email.com"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].role").value("ADMIN"));

        verify(usuarioService, times(1)).getAllUsuarios();
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllUsuarios_DeveRetornar403_QuandoNaoForAdmin() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).getAllUsuarios();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_DeveRetornar200_QuandoIdExistirEForAdmin() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");
        usuario.setNome("Usuário Teste");
        usuario.setRole(UserRole.USER);

        when(usuarioService.findById(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("usuario@email.com"))
                .andExpect(jsonPath("$.nome").value("Usuário Teste"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(usuarioService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void findById_DeveRetornar404_QuandoIdNaoExistirEForAdmin() throws Exception {
        when(usuarioService.findById(99L)).thenThrow(new UsuarioNaoEncontradoException("Usuario nao encontrado"));

        mockMvc.perform(get("/usuarios/{id}", 99L))
                .andExpect(status().isNotFound());

        verify(usuarioService, times(1)).findById(99L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_DeveRetornar403_QuandoNaoForAdmin() throws Exception {
        mockMvc.perform(get("/usuarios/{id}", 1L))
                .andExpect(status().isForbidden());

        verify(usuarioService, never()).findById(any());
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void atualizarUsuario_DeveRetornar200_QuandoDadosForemValidos() throws Exception {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setEmail("usuario@email.com");
        usuarioLogado.setNome("Usuário Antigo");
        usuarioLogado.setRole(UserRole.USER);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "usuario@email.com",
                null,
                "Usuário Atualizado"
        );

        Usuario usuarioAtualizado = new Usuario();
        usuarioAtualizado.setId(1L);
        usuarioAtualizado.setEmail("usuario@email.com");
        usuarioAtualizado.setNome("Usuário Atualizado");
        usuarioAtualizado.setRole(UserRole.USER);

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuarioLogado));
        when(usuarioService.atualizarUsuario(any(Usuario.class), any(UsuarioRequestDTO.class))).thenReturn(usuarioAtualizado);

        mockMvc.perform(put("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("usuario@email.com"))
                .andExpect(jsonPath("$.nome").value("Usuário Atualizado"));

        verify(usuarioService, times(1)).atualizarUsuario(any(Usuario.class), any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void atualizarUsuario_DeveRetornar409_QuandoNovoEmailJaExistir() throws Exception {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setEmail("usuario@email.com");
        usuarioLogado.setRole(UserRole.USER);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "emailjaexiste@email.com",
                null,
                "Usuário"
        );

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuarioLogado));
        when(usuarioService.atualizarUsuario(any(Usuario.class), any(UsuarioRequestDTO.class)))
                .thenThrow(new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email."));

        mockMvc.perform(put("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());

        verify(usuarioService, times(1)).atualizarUsuario(any(Usuario.class), any(UsuarioRequestDTO.class));
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void atualizarUsuario_DeveRetornar400_QuandoDadosForemInvalidos() throws Exception {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setEmail("usuario@email.com");
        usuarioLogado.setRole(UserRole.USER);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "",
                "",
                ""
        );

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuarioLogado));

        mockMvc.perform(put("/usuarios/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).atualizarUsuario(any(), any());
    }

    @Test
    @WithMockUser(username = "usuario@email.com", roles = "USER")
    void deleteUsuario_DeveRetornar204_QuandoUsuarioAutenticado() throws Exception {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setEmail("usuario@email.com");
        usuarioLogado.setRole(UserRole.USER);

        when(usuarioRepository.findByEmail("usuario@email.com")).thenReturn(java.util.Optional.of(usuarioLogado));
        doNothing().when(usuarioService).deletarUsuario(any(Usuario.class));

        mockMvc.perform(delete("/usuarios/me"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).deletarUsuario(any(Usuario.class));
    }
}
