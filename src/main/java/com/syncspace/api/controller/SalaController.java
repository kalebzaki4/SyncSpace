package com.syncspace.api.controller;

import com.syncspace.api.dto.SalaRequestDTO;
import com.syncspace.api.dto.SalaResponseDTO;
import com.syncspace.api.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> findAll() {
        return ResponseEntity.ok(salaService.findAll());
    }

    @PostMapping
    public ResponseEntity<SalaResponseDTO> create(
            @RequestBody @Valid SalaRequestDTO request,
            UriComponentsBuilder uriBuilder
    ) {
        SalaResponseDTO sala = salaService.create(request);
        return ResponseEntity.created(uriBuilder.path("/salas/{id}").buildAndExpand(sala.id()).toUri())
                .body(sala);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalaResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid SalaRequestDTO request
    ) {
        return ResponseEntity.ok(salaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        salaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
