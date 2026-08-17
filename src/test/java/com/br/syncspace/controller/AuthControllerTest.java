package com.br.syncspace.controller;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.domain.usuario.UsuarioService;
import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = SecurityFilter.class
        )
)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void register_DeveRetornar201_QuandoDadosForemValidos() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "novo@email.com",
                "Senha@123",
                "Novo Usuário"
        );

        Usuario usuarioCriado = new Usuario();
        usuarioCriado.setId(1L);
        usuarioCriado.setEmail("novo@email.com");
        usuarioCriado.setNome("Novo Usuário");
        usuarioCriado.setRole(UserRole.USER);

        when(usuarioService.criarUsuario(any(UsuarioRequestDTO.class))).thenReturn(usuarioCriado);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("novo@email.com"))
                .andExpect(jsonPath("$.nome").value("Novo Usuário"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(usuarioService, times(1)).criarUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void register_DeveRetornar409_QuandoEmailJaEstiverCadastrado() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "existente@email.com",
                "Senha@123",
                "Usuário"
        );

        when(usuarioService.criarUsuario(any(UsuarioRequestDTO.class)))
                .thenThrow(new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email."));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());

        verify(usuarioService, times(1)).criarUsuario(any(UsuarioRequestDTO.class));
    }

    @Test
    void register_DeveRetornar400_QuandoDadosForemInvalidos() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "",
                "",
                ""
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(usuarioService, never()).criarUsuario(any());
    }

    @Test
    void login_DeveRetornar200ComToken_QuandoCredenciaisForemValidas() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "usuario@email.com",
                "Senha@123",
                ""
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");
        usuario.setRole(UserRole.USER);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(usuario);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenService.generateToken(usuario)).thenReturn("token-jwt-gerado");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-jwt-gerado"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, times(1)).generateToken(usuario);
    }

    @Test
    void login_DeveRetornar401_QuandoCredenciaisForemInvalidas() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "usuario@email.com",
                "SenhaErrada@123",
                ""
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void login_DeveRetornar400_QuandoDadosForemInvalidos() throws Exception {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "",
                "",
                ""
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(authenticationManager, never()).authenticate(any());
    }
}
