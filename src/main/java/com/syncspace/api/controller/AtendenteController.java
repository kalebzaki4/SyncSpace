package com.syncspace.api.controller;

import com.syncspace.api.dto.AtendenteRequestDTO;
import com.syncspace.api.dto.AtendenteResponseDTO;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.service.AtendenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<List<AtendenteResponseDTO>> findAll(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(atendenteService.findAllAtendentes(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendenteResponseDTO> findById(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(atendenteService.findById(id, usuario));
    }

    @PostMapping
    public ResponseEntity<AtendenteResponseDTO> createAtendente(
            @RequestBody @Valid AtendenteRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriBuilder
    ) {
        AtendenteResponseDTO atendenteSalvo = atendenteService.createAtendente(requestDTO, usuario);
        var uri = uriBuilder.path("/atendentes/{id}").buildAndExpand(atendenteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(atendenteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtendenteResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AtendenteRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(atendenteService.updateAtendente(id, requestDTO, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        atendenteService.deleteAtendente(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
