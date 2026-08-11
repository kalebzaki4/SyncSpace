package com.br.syncspace.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.br.syncspace.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {
    @Value("${security.password.encoder.secret}")
    private String secret;

    public String generateToken(Usuario usuario) {
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.create().withIssuer("SyncSpace").withClaim("id", usuario.getId()).withExpiresAt(expiration()).withSubject(usuario.getEmail()).sign(algorithm);
    }

    public String getSubject(String token) {
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("SyncSpace")
                .build()
                .verify(token)
                .getSubject();
    }

    private Instant expiration() {
        return Instant.now().plusSeconds(3600);
    }
}
