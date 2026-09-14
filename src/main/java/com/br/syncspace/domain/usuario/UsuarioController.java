package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.domain.usuario.dto.UsuarioResponseDTO;
import com.br.syncspace.infra.exception.UsuarioNaoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioService usuarioService, UsuarioRepository usuarioRepository) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioService.getAllUsuarios();
        return ResponseEntity.ok(
                usuarios.stream().map(UsuarioResponseDTO::new).toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> findById(@PathVariable Long id) {
        Usuario usuarioEncontrado = usuarioService.findById(id);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioEncontrado));
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UsuarioResponseDTO> atualizarUsuario(
            @RequestBody @Valid UsuarioRequestDTO requestDTO
    ) {
        Usuario usuarioLogado = resolverUsuario();
        Usuario usuarioAtualizado = usuarioService.atualizarUsuario(usuarioLogado, requestDTO);
        return ResponseEntity.ok(new UsuarioResponseDTO(usuarioAtualizado));
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteUsuario() {
        Usuario usuarioLogado = resolverUsuario();
        usuarioService.deletarUsuario(usuarioLogado);
        return ResponseEntity.noContent().build();
    }

    private Usuario resolverUsuario() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Usuario usuario) {
            return usuario;
        }

        String username = authentication.getName();
        if (username != null && !username.isBlank()) {
            return usuarioRepository.findByEmail(username)
                    .orElseGet(() -> {
                        Usuario usuarioFallback = new Usuario();
                        usuarioFallback.setEmail(username);
                        usuarioFallback.setRole(UserRole.USER);
                        return usuarioFallback;
                    });
        }

        throw new UsuarioNaoEncontradoException("Usuario nao encontrado");
    }
}