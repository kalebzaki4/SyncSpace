package com.syncspace.api.controller;

import com.syncspace.api.dto.MedicoRequestDTO;
import com.syncspace.api.dto.MedicoResponseDTO;
import com.syncspace.api.dto.PacienteResponseDTO;
import com.syncspace.api.dto.PacienteRequestDTO;
import com.syncspace.api.service.MedicoService;
import com.syncspace.api.service.PacienteService;
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
    private final PacienteService pacienteService;

    public AuthController(MedicoService medicoService, PacienteService pacienteService) {
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
    }

    @PostMapping("/register/medico")
    public ResponseEntity<MedicoResponseDTO> registerMedico(@RequestBody @Valid MedicoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        MedicoResponseDTO medicoSalvo = medicoService.createMedico(requestDTO);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medicoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(medicoSalvo);
    }

    @PostMapping("/register/paciente")
    public ResponseEntity<PacienteResponseDTO> registerPaciente(@RequestBody @Valid PacienteRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        PacienteResponseDTO pacienteSalvo = pacienteService.createPaciente(requestDTO);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(pacienteSalvo);
    }
}
