package com.syncspace.api.controller;

import com.syncspace.api.dto.ReservaRequestDTO;
import com.syncspace.api.dto.ReservaResponseDTO;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {
    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @GetMapping("/minhas-consultas")
    public ResponseEntity<List<ReservaResponseDTO>> findMyConsultations(
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(consultaService.findMyConsultations(usuario));
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> create(
            @RequestBody @Valid ReservaRequestDTO request,
            UriComponentsBuilder uriBuilder
    ) {
        ReservaResponseDTO reserva = consultaService.create(request);
        return ResponseEntity.created(uriBuilder.path("/consultas/{id}").buildAndExpand(reserva.id()).toUri())
                .body(reserva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid ReservaRequestDTO request
    ) {
        return ResponseEntity.ok(consultaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        consultaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
