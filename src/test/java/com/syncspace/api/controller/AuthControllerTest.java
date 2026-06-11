package com.syncspace.api.controller;

import com.syncspace.api.domain.usuario.Usuario;
import com.syncspace.api.domain.usuario.dto.DadosLogin;
import com.syncspace.api.domain.usuario.dto.DadosToken;
import com.syncspace.api.infra.security.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("Deve realizar login e retornar token")
    void deveRealizarLogin() {
        DadosLogin dados = new DadosLogin("teste@teste.com", "123456");
        Authentication authMock = mock(Authentication.class);
        Usuario usuario = new Usuario();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authMock);
        when(authMock.getPrincipal()).thenReturn(usuario);
        when(tokenService.gerarToken(usuario)).thenReturn("token_jwt_valido");

        ResponseEntity<DadosToken> resposta = authController.login(dados);

        Assertions.assertEquals(HttpStatus.OK, resposta.getStatusCode());
        Assertions.assertEquals("token_jwt_valido", resposta.getBody().token());
    }
}