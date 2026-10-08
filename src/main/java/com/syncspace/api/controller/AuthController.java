package com.syncspace.api.controller;

import com.syncspace.api.dto.MedicoRequestDTO;
import com.syncspace.api.dto.MedicoResponseDTO;
import com.syncspace.api.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final MedicoService medicoService;

    public AuthController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping("/register/medico")
    public ResponseEntity<MedicoResponseDTO> registerMedico(@RequestBody @Valid MedicoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        MedicoResponseDTO medicoSalvo = medicoService.createMedico(requestDTO);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medicoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(medicoSalvo);
    }
}
