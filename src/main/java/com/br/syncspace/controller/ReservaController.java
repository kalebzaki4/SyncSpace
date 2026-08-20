package com.br.syncspace.controller;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.domain.reserva.ReservaService;
import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.reserva.dto.ReservaResponseDTO;
import com.br.syncspace.domain.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
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
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasUsuario(
            @AuthenticationPrincipal Usuario usuario
    ) {
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
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {
        Reserva novaReserva = reservaService.criarReserva(usuario, reservaRequestDTO);
        URI uri = URI.create("/reservas/" + novaReserva.getId());

        return ResponseEntity
                .created(uri)
                .body(new ReservaResponseDTO(novaReserva));
    }

    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservaResponseDTO> atualizarReserva(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {
        Reserva reservaAtualizada = reservaService.atualizarReserva(usuario, reservaRequestDTO);
        return ResponseEntity.ok(new ReservaResponseDTO(reservaAtualizada));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deletarReserva(
            @AuthenticationPrincipal Usuario usuario,
            @PathVariable Long id
    ) {
        reservaService.deletarReserva(usuario, id);
        return ResponseEntity.noContent().build();
    }
}