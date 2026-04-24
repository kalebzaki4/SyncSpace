package com.syncspace.api.controller;

import com.syncspace.api.dto.DadosCadastroUsuario;
import com.syncspace.api.dto.DadosAtualizacaoUsuario;
import com.syncspace.api.service.UsuarioService;
import com.syncspace.api.model.Usuario;
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

    // ver todos os usuários
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.findAllUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // ver usuário específico
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Long id) {
        Usuario usuario = usuarioService.findUsuarioById(id);
        return usuario != null ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    // criar usuário
    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody @Valid DadosCadastroUsuario dadosCadastro,
                                                 UriComponentsBuilder uriComponentsBuilder) {
        var usuario = new Usuario();
        usuario.setNome(dadosCadastro.nome());
        usuario.setEmail(dadosCadastro.email());
        usuario.setSenha(dadosCadastro.senha());
        Usuario novoUsuario = usuarioService.createUsuario(usuario);

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(novoUsuario);
    }

    // atualizar usuário
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Long id,
                                                 @RequestBody @Valid DadosAtualizacaoUsuario dados) {
        Usuario updatedUsuario = usuarioService.updateUsuario(id, dados);
        return updatedUsuario != null ? ResponseEntity.ok(updatedUsuario) : ResponseEntity.notFound().build();
    }

    // deletar usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}