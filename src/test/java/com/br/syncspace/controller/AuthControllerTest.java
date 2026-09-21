package com.br.syncspace.controller;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.domain.usuario.CadastroService;
import com.br.syncspace.domain.medico.dto.CadastroMedicoRequestDTO;
import com.br.syncspace.domain.paciente.dto.CadastroPacienteRequestDTO;
import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.security.AuthController;
import com.br.syncspace.infra.security.TokenService;
import com.br.syncspace.infra.security.dto.LoginRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CadastroService cadastroService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @Test
    void registerPaciente_DeveRetornar201_QuandoDadosForemValidos() throws Exception {
        CadastroPacienteRequestDTO requestDTO = new CadastroPacienteRequestDTO(
                "novo@email.com",
                "Senha@123",
                "Novo Usuário",
                "12345678900",
                java.time.LocalDate.of(1990, 1, 1)
        );

        Usuario usuarioCriado = new Usuario();
        usuarioCriado.setId(1L);
        usuarioCriado.setEmail("novo@email.com");
        usuarioCriado.setNome("Novo Usuário");
        usuarioCriado.setRole(UserRole.PACIENTE);

        when(cadastroService.cadastrarPaciente(any(CadastroPacienteRequestDTO.class))).thenReturn(usuarioCriado);

        mockMvc.perform(post("/auth/register/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", "http://localhost/usuarios/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("novo@email.com"))
                .andExpect(jsonPath("$.nome").value("Novo Usuário"))
                .andExpect(jsonPath("$.role").value("PACIENTE"));

        verify(cadastroService, times(1)).cadastrarPaciente(any(CadastroPacienteRequestDTO.class));
    }

    @Test
    void registerMedico_DeveRetornar201_QuandoDadosForemValidos() throws Exception {
        CadastroMedicoRequestDTO requestDTO = new CadastroMedicoRequestDTO(
                "medico@email.com",
                "Senha@123",
                "Dra. Ana",
                "CRM-12345",
                "Cardiologia"
        );

        Usuario usuarioCriado = new Usuario();
        usuarioCriado.setId(2L);
        usuarioCriado.setEmail("medico@email.com");
        usuarioCriado.setNome("Dra. Ana");
        usuarioCriado.setRole(UserRole.MEDICO);

        when(cadastroService.cadastrarMedico(any(CadastroMedicoRequestDTO.class))).thenReturn(usuarioCriado);

        mockMvc.perform(post("/auth/register/medico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/usuarios/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.role").value("MEDICO"));

        verify(cadastroService, times(1)).cadastrarMedico(any(CadastroMedicoRequestDTO.class));
    }

    @Test
    void registerMedico_DeveRetornar409_QuandoEmailJaEstiverCadastrado() throws Exception {
        CadastroMedicoRequestDTO requestDTO = new CadastroMedicoRequestDTO(
                "existente@email.com",
                "Senha@123",
                "Usuário",
                "CRM-12345",
                "Cardiologia"
        );

        when(cadastroService.cadastrarMedico(any(CadastroMedicoRequestDTO.class)))
                .thenThrow(new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email."));

        mockMvc.perform(post("/auth/register/medico")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isConflict());

        verify(cadastroService, times(1)).cadastrarMedico(any(CadastroMedicoRequestDTO.class));
    }

    @Test
    void register_DeveRetornar400_QuandoDadosForemInvalidos() throws Exception {
        CadastroPacienteRequestDTO requestDTO = new CadastroPacienteRequestDTO(
                "",
                "",
                "",
                "",
                null
        );

        mockMvc.perform(post("/auth/register/paciente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        verify(cadastroService, never()).cadastrarPaciente(any());
    }

    @Test
    void login_DeveRetornar200ComToken_QuandoCredenciaisForemValidas() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO(
                "usuario@email.com",
                "Senha@123"
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
        LoginRequestDTO requestDTO = new LoginRequestDTO(
                "usuario@email.com",
                "SenhaErrada@123"
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
    void login_DeveRetornar400_QuandoCredenciaisForemInvalidas() throws Exception {
        LoginRequestDTO requestDTO = new LoginRequestDTO(
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
