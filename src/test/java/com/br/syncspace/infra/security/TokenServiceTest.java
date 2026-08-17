package com.br.syncspace.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private static final String SECRET = "secret-test-syncspace-123456";
    private static final String ISSUER = "SyncSpace";

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", SECRET);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");
        usuario.setNome("Usuário Teste");
        usuario.setRole(UserRole.USER);
    }

    @Test
    void generateToken_DeveGerarTokenValido() {
        String token = tokenService.generateToken(usuario);

        assertNotNull(token);
        assertFalse(token.isBlank());

        var algorithm = Algorithm.HMAC256(SECRET);
        var verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        var decodedJWT = verifier.verify(token);

        assertEquals("usuario@email.com", decodedJWT.getSubject());
        assertEquals(1L, decodedJWT.getClaim("id").asLong());
        assertEquals(ISSUER, decodedJWT.getIssuer());
    }

    @Test
    void generateToken_DeveGerarTokenComExpiracaoDeUmaHora() {
        Instant antes = Instant.now().plusSeconds(3599);
        String token = tokenService.generateToken(usuario);
        Instant depois = Instant.now().plusSeconds(3601);

        var algorithm = Algorithm.HMAC256(SECRET);
        var verifier = JWT.require(algorithm).withIssuer(ISSUER).build();
        var decodedJWT = verifier.verify(token);

        Instant expiracao = decodedJWT.getExpiresAt().toInstant();
        assertTrue(expiracao.isAfter(antes),
                "Expiração deveria ser depois de " + antes + " mas foi " + expiracao);
        assertTrue(expiracao.isBefore(depois),
                "Expiração deveria ser antes de " + depois + " mas foi " + expiracao);
    }

    @Test
    void generateToken_DeveGerarTokensDiferentesParaUsuariosDiferentes() {
        Usuario usuario2 = new Usuario();
        usuario2.setId(2L);
        usuario2.setEmail("outro@email.com");
        usuario2.setRole(UserRole.ADMIN);

        String token1 = tokenService.generateToken(usuario);
        String token2 = tokenService.generateToken(usuario2);

        assertNotEquals(token1, token2);

        var algorithm = Algorithm.HMAC256(SECRET);
        var verifier = JWT.require(algorithm).withIssuer(ISSUER).build();

        var decoded1 = verifier.verify(token1);
        var decoded2 = verifier.verify(token2);

        assertEquals("usuario@email.com", decoded1.getSubject());
        assertEquals("outro@email.com", decoded2.getSubject());
        assertEquals(1L, decoded1.getClaim("id").asLong());
        assertEquals(2L, decoded2.getClaim("id").asLong());
    }

    @Test
    void getSubject_DeveRetornarEmailDoTokenValido() {
        var algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withClaim("id", usuario.getId())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .withSubject(usuario.getEmail())
                .sign(algorithm);

        String subject = tokenService.getSubject(token);

        assertEquals("usuario@email.com", subject);
    }

    @Test
    void getSubject_DeveLancarExcecao_QuandoTokenForInvalido() {
        String tokenInvalido = "token.invalido.abc123";

        assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(tokenInvalido));
    }

    @Test
    void getSubject_DeveLancarExcecao_QuandoTokenForAssinadoComSecretErrado() {
        var algorithmErrado = Algorithm.HMAC256("secret-errado-diferente");
        String token = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(algorithmErrado);

        assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(token));
    }

    @Test
    void getSubject_DeveLancarExcecao_QuandoIssuerForDiferente() {
        var algorithm = Algorithm.HMAC256(SECRET);
        String token = JWT.create()
                .withIssuer("IssuerErrado")
                .withSubject(usuario.getEmail())
                .withExpiresAt(Instant.now().plusSeconds(3600))
                .sign(algorithm);

        assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(token));
    }

    @Test
    void getSubject_DeveLancarExcecao_QuandoTokenEstiverExpirado() throws InterruptedException {
        var algorithm = Algorithm.HMAC256(SECRET);
        String tokenExpirado = JWT.create()
                .withIssuer(ISSUER)
                .withSubject(usuario.getEmail())
                .withExpiresAt(Instant.now().plusMillis(50))
                .sign(algorithm);

        Thread.sleep(100);

        assertThrows(JWTVerificationException.class, () -> tokenService.getSubject(tokenExpirado));
    }
}
