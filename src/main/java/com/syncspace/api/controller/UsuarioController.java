package com.syncspace.api.controller;

import com.syncspace.api.dto.UsuarioRequestDTO;
import com.syncspace.api.dto.UsuarioResponseDTO;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> create(
            @RequestBody @Valid UsuarioRequestDTO request,
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            UriComponentsBuilder uriBuilder
    ) {
        Usuario usuario = usuarioService.createUsuario(request, usuarioAutenticado);
        var uri = uriBuilder.path("/api/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioResponseDTO(usuario));
    }
}
