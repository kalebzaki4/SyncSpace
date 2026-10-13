package com.syncspace.api.controller;

import com.syncspace.api.dto.*;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.service.AtendenteService;
import com.syncspace.api.service.MedicoService;
import com.syncspace.api.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final AtendenteService atendenteService;

    public AuthController(MedicoService medicoService, PacienteService pacienteService, AtendenteService atendenteService) {
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.atendenteService = atendenteService;
    }

    // criar medico
    @PostMapping("/register/medico")
    public ResponseEntity<MedicoResponseDTO> registerMedico(@RequestBody @Valid MedicoRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        MedicoResponseDTO medicoSalvo = medicoService.createMedico(requestDTO);
        var uri = uriBuilder.path("/medicos/{id}").buildAndExpand(medicoSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(medicoSalvo);
    }

    // criar paciente
    @PostMapping("/register/paciente")
    public ResponseEntity<PacienteResponseDTO> registerPaciente(@RequestBody @Valid PacienteRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        PacienteResponseDTO pacienteSalvo = pacienteService.createPaciente(requestDTO);
        var uri = uriBuilder.path("/pacientes/{id}").buildAndExpand(pacienteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(pacienteSalvo);
    }

    // criar atendente
    @PostMapping("/register/atendente")
    public ResponseEntity<AtendenteResponseDTO> registerAtendente(@RequestBody @Valid AtendenteRequestDTO requestDTO, UriComponentsBuilder uriBuilder) {
        AtendenteResponseDTO atendenteSalvo = atendenteService.createAtendente(requestDTO);
        var uri = uriBuilder.path("/atendentes/{id}").buildAndExpand(atendenteSalvo.id()).toUri();
        return ResponseEntity.created(uri).body(atendenteSalvo);
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody @Valid LoginRequestDTO dados) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new TokenDTO(tokenJWT));
    }

    }


