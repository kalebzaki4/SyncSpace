package com.syncspace.api.controller;

import com.syncspace.api.dto.AtendenteRequestDTO;
import com.syncspace.api.dto.AtendenteResponseDTO;
import com.syncspace.api.service.AtendenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/atendentes")
public class AtendenteController {
    private final AtendenteService atendenteService;

    public AtendenteController(AtendenteService atendenteService) {
        this.atendenteService = atendenteService;
    }

    @GetMapping
    public ResponseEntity<List<AtendenteResponseDTO>> findAll() {
        return ResponseEntity.ok(atendenteService.findAllAtendentes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendenteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(atendenteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AtendenteResponseDTO> createAtendente(
            @RequestBody @Valid AtendenteRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        AtendenteResponseDTO atendenteSalvo = atendenteService.createAtendente(requestDTO);
        var uri = uriBuilder.path("/atendentes/{id}").buildAndExpand(atendenteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(atendenteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendenteResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AtendenteRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(atendenteService.updateAtendente(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        atendenteService.deleteAtendente(id);
        return ResponseEntity.noContent().build();
    }
}
