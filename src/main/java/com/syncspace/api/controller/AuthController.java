package com.syncspace.api.controller;

import com.syncspace.api.dto.LoginRequestDTO;
import com.syncspace.api.dto.TokenDTO;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final TokenService tokenService;
    private final AuthenticationManager manager;

    public AuthController(TokenService tokenService, AuthenticationManager manager) {
        this.tokenService = tokenService;
        this.manager = manager;
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginRequestDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(tokenJWT));
    }

}