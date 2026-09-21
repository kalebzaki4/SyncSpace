package com.br.syncspace.infra.security;

import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.domain.usuario.CadastroService;
import com.br.syncspace.domain.medico.dto.CadastroMedicoRequestDTO;
import com.br.syncspace.domain.paciente.dto.CadastroPacienteRequestDTO;
import com.br.syncspace.domain.usuario.dto.UsuarioResponseDTO;
import com.br.syncspace.infra.security.dto.LoginRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final CadastroService cadastroService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(CadastroService cadastroService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.cadastroService = cadastroService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register/paciente")
    public ResponseEntity<UsuarioResponseDTO> registerPaciente(@RequestBody @Valid CadastroPacienteRequestDTO usuarioRequestDTO,
                                                                 UriComponentsBuilder uriComponentsBuilder) {
        return criarUsuario(cadastroService.cadastrarPaciente(usuarioRequestDTO), uriComponentsBuilder);
    }

    @PostMapping("/register/medico")
    public ResponseEntity<UsuarioResponseDTO> registerMedico(@RequestBody @Valid CadastroMedicoRequestDTO usuarioRequestDTO,
                                                               UriComponentsBuilder uriComponentsBuilder) {
        return criarUsuario(cadastroService.cadastrarMedico(usuarioRequestDTO), uriComponentsBuilder);
    }

    private ResponseEntity<UsuarioResponseDTO> criarUsuario(Usuario usuario, UriComponentsBuilder uriComponentsBuilder) {

        var uri = uriComponentsBuilder.path("/usuarios/{id}").buildAndExpand(usuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UsuarioResponseDTO(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<DadosTokenJwtDto> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        var usuarioLogin = new UsernamePasswordAuthenticationToken(loginRequestDTO.email(), loginRequestDTO.password());
        var authentication = authenticationManager.authenticate(usuarioLogin);
        var token = tokenService.generateToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosTokenJwtDto(token));
    }
}
