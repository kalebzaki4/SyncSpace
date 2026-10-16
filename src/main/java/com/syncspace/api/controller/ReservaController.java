package com.syncspace.api.controller;

import com.syncspace.api.dto.ReservaRequestDTO;
import com.syncspace.api.dto.ReservaResponseDTO;
import com.syncspace.api.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ReservaController {
    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping("/minhas-consultas")
    public ResponseEntity<List<ReservaResponseDTO>> findMyConsultations() {
        return ResponseEntity.ok(reservaService.findMyConsultations());
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> create(
            @RequestBody @Valid ReservaRequestDTO request,
            UriComponentsBuilder uriBuilder
    ) {
        ReservaResponseDTO reserva = reservaService.create(request);
        return ResponseEntity.created(uriBuilder.path("/consultas/{id}").buildAndExpand(reserva.id()).toUri())
                .body(reserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequestDTO request
    ) {
        return ResponseEntity.ok(reservaService.update(id, request));
    }
}
