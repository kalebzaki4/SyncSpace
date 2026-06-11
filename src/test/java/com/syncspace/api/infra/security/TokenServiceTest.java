package com.syncspace.api.infra.security;

import com.syncspace.api.domain.usuario.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", "minha-chave-secreta-de-teste");
    }

    @Test
    @DisplayName("Deve gerar token válido")
    void deveGerarToken() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@syncspace.com");

        String token = tokenService.gerarToken(usuario);

        Assertions.assertNotNull(token);
        Assertions.assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Deve validar token e extrair o email")
    void deveValidarToken() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("teste@syncspace.com");
        String token = tokenService.gerarToken(usuario);

        String subject = tokenService.ValidarToken(token);

        Assertions.assertEquals("teste@syncspace.com", subject);
    }
}
