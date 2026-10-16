package com.syncspace.api.controller;

import com.syncspace.api.dto.PacienteRequestDTO;
import com.syncspace.api.dto.PacienteResponseDTO;
import com.syncspace.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> findAll() {
        return ResponseEntity.ok(pacienteService.findAllPacientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pacienteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> createPaciente(
            @RequestBody @Valid PacienteRequestDTO requestDTO,
            UriComponentsBuilder uriBuilder
    ) {
        PacienteResponseDTO pacienteSalvo = pacienteService.createPaciente(requestDTO);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(pacienteSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid PacienteRequestDTO requestDTO
    ) {
        return ResponseEntity.ok(pacienteService.updatePaciente(id, requestDTO));
    }

}
