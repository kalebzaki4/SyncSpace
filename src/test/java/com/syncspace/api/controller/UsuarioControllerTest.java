package com.syncspace.api.controller;

import com.syncspace.api.domain.usuario.Usuario;
import com.syncspace.api.domain.usuario.UsuarioService;
import com.syncspace.api.domain.usuario.dto.DadosCadastroUsuario;
import com.syncspace.api.domain.usuario.dto.UsuarioResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    @DisplayName("Deve criar usuário e retornar 201 Created")
    void deveCriarUsuario() {
        DadosCadastroUsuario dados = new DadosCadastroUsuario("Nome", "email@teste.com", "senha123");
        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome(dados.nome());
        usuarioSalvo.setEmail(dados.email());

        when(usuarioService.createUsuario(any(Usuario.class))).thenReturn(usuarioSalvo);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<UsuarioResponseDTO> resposta = usuarioController.createUsuario(dados, uriBuilder);

        Assertions.assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        Assertions.assertEquals("Nome", resposta.getBody().nome());
        Assertions.assertNotNull(resposta.getHeaders().getLocation());
    }

    @Test
    @DisplayName("Deve deletar usuário e retornar 204 No Content")
    void deveDeletarUsuario() {
        Long id = 1L;

        ResponseEntity<Void> resposta = usuarioController.deleteUsuario(id);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        verify(usuarioService, times(1)).deleteUsuario(id);
    }
}