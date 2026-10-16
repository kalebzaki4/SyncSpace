package com.syncspace.api.controller;

import com.syncspace.api.dto.MedicoRequestDTO;
import com.syncspace.api.dto.MedicoResponseDTO;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {
    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> findAll() {
        return ResponseEntity.ok(medicoService.findAllMedicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> createMedico(
            @RequestBody @Valid MedicoRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuario,
            UriComponentsBuilder uriBuilder
    ) {
        MedicoResponseDTO medicoSalvo = medicoService.createMedico(requestDTO, usuario);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medicoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(medicoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid MedicoRequestDTO requestDTO,
            @AuthenticationPrincipal Usuario usuario
    ) {
        return ResponseEntity.ok(medicoService.updateMedico(id, requestDTO, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, @AuthenticationPrincipal Usuario usuario) {
        medicoService.deleteMedico(id, usuario);
        return ResponseEntity.noContent().build();
    }
}
