package com.syncspace.api.security;

import com.syncspace.api.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.time.Instant;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer("API SyncSpace")
                .withSubject(usuario.getEmail())
                .withExpiresAt(dataExpiracao())
                .sign(Algorithm.HMAC256(secret));

    }

    public String getSubject(String tokenJWT) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("API SyncSpace")
                .build()
                .verify(tokenJWT)
                .getSubject();
    }

    private Instant dataExpiracao() {
        return Instant.now().plusSeconds(7200);
    }
}
