package com.syncspace.api.controller;

import com.syncspace.api.domain.usuario.Usuario;
import com.syncspace.api.domain.usuario.dto.DadosAtualizacaoUsuario;
import com.syncspace.api.domain.usuario.dto.DadosCadastroUsuario;
import com.syncspace.api.domain.usuario.dto.UsuarioResponseDTO;
import com.syncspace.api.domain.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // listar todos os usuários
    @GetMapping
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<UsuarioResponseDTO> usuarios = usuarioService.findAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // buscar usuário por ID
    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findUsuarioById(id);

        UsuarioResponseDTO response = new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getEmail());

        return ResponseEntity.ok(response);
    }

    // criar usuário
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> createUsuario(@RequestBody @Valid DadosCadastroUsuario dadosCadastro, UriComponentsBuilder uriComponentsBuilder) {
        var usuario = new Usuario();
        usuario.setNome(dadosCadastro.nome());
        usuario.setEmail(dadosCadastro.email());
        usuario.setSenha(dadosCadastro.senha());

        Usuario novoUsuario = usuarioService.createUsuario(usuario);

        UsuarioResponseDTO response = new UsuarioResponseDTO(novoUsuario.getId(), novoUsuario.getNome(), novoUsuario.getEmail());

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(novoUsuario.getId()).toUri();

        return ResponseEntity.created(uri).body(response);
    }

    // atualizar usuário
    @PutMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Long id, @RequestBody @Valid DadosAtualizacaoUsuario dados) {
        Usuario updatedUsuario = usuarioService.updateUsuario(id, dados);

        if (updatedUsuario == null) {
            return ResponseEntity.notFound().build();
        }

        UsuarioResponseDTO response = new UsuarioResponseDTO(updatedUsuario.getId(), updatedUsuario.getNome(), updatedUsuario.getEmail());

        return ResponseEntity.ok(response);
    }

    // deletar usuário
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}