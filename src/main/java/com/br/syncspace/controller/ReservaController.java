package com.br.syncspace.controller;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.domain.reserva.ReservaService;
import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.reserva.dto.ReservaResponseDTO;
import com.br.syncspace.domain.usuario.UserRole;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.UsuarioRepository;
import com.br.syncspace.infra.exception.UsuarioNaoEncontradoException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UsuarioRepository usuarioRepository;

    public ReservaController(ReservaService reservaService, UsuarioRepository usuarioRepository) {
        this.reservaService = reservaService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservaResponseDTO>> listarReservas(
            @PageableDefault(size = 10, sort = "dataHoraInicio") Pageable pageable
    ) {
        Page<ReservaResponseDTO> page = reservaService.listarReservas(pageable)
                .map(ReservaResponseDTO::new);

        return ResponseEntity.ok(page);
    }

    @GetMapping("/usuario")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasUsuario() {
        Usuario usuario = resolverUsuario();
        List<ReservaResponseDTO> reservas = reservaService
                .listarReservasPorUsuario(usuario.getId())
                .stream()
                .map(ReservaResponseDTO::new)
                .toList();

        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaResponseDTO> criarReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {
        Usuario usuario = resolverUsuario();
        Reserva novaReserva = reservaService.criarReserva(usuario, reservaRequestDTO);
        URI uri = URI.create("/reservas/" + novaReserva.getId());

        return ResponseEntity
                .created(uri)
                .body(new ReservaResponseDTO(novaReserva));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaResponseDTO> atualizarReserva(
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {
        Usuario usuario = resolverUsuario();
        Reserva reservaAtualizada = reservaService.atualizarReserva(usuario, reservaRequestDTO);
        return ResponseEntity.ok(new ReservaResponseDTO(reservaAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletarReserva(@PathVariable Long id) {
        Usuario usuario = resolverUsuario();
        reservaService.deletarReserva(usuario, id);
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