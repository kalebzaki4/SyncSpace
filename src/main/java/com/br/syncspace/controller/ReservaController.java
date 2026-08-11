package com.br.syncspace.controller;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.domain.reserva.ReservaService;
import com.br.syncspace.domain.reserva.dto.ReservaRequestDTO;
import com.br.syncspace.domain.reserva.dto.ReservaResponseDTO;
import com.br.syncspace.domain.usuario.Usuario;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ReservaResponseDTO>> listarReservas() {
        List<ReservaResponseDTO> reservas = reservaService.listarReservas()
                .stream()
                .map(ReservaResponseDTO::new)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/usuario")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasUsuario(@AuthenticationPrincipal Usuario usuario) {
        List<ReservaResponseDTO> reservas = reservaService.listarReservasPorUsuario(usuario.getId())
                .stream()
                .map(ReservaResponseDTO::new)
                .toList();
        return ResponseEntity.ok(reservas);
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criarReserva(
            @AuthenticationPrincipal Usuario usuario,
            @Valid @RequestBody ReservaRequestDTO reservaRequestDTO
    ) {
        Reserva novaReserva = reservaService.criarReserva(usuario, reservaRequestDTO);
        URI uri = URI.create("/reservas/" + novaReserva.getId());
        return ResponseEntity.created(uri).body(new ReservaResponseDTO(novaReserva));
    }
}