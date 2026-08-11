package com.br.syncspace.controller;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioService;
import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.domain.usuario.dto.UsuarioResponseDTO;
import com.br.syncspace.infra.security.DadosTokenJwtDto;
import com.br.syncspace.infra.security.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO, UriComponentsBuilder uriComponentsBuilder) {

        Usuario usuario = usuarioService.criarUsuario(usuarioRequestDTO);
        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();

        return ResponseEntity.created(uri).body(new UsuarioResponseDTO(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJwtDto> login(@RequestBody @Valid UsuarioRequestDTO usuarioRequestDTO) {
        var usuarioLogin = new UsernamePasswordAuthenticationToken(usuarioRequestDTO.email(), usuarioRequestDTO.password());
        var authentication = authenticationManager.authenticate(usuarioLogin);
        var token = tokenService.generateToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJwtDto(token));
    }
}