package com.br.syncspace.controller;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioService;
import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.domain.usuario.dto.UsuarioResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioResponseDTO::new).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        Usuario usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioEncontrado));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @AuthenticationPrincipal Usuario usuarioLogado,
            @RequestBody @Valid UsuarioRequestDTO requestDTO) {

        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(usuarioLogado, requestDTO);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioAtualizado));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUsuario(@AuthenticationPrincipal Usuario usuarioLogado) {
        usuarioService.deletarUsuario(usuarioLogado);
        return ResponseEntity.noContent().build();
    }
}