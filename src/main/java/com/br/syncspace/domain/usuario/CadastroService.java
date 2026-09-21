package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.medico.Medico;
import com.br.syncspace.domain.medico.MedicoRepository;
import com.br.syncspace.domain.medico.dto.CadastroMedicoRequestDTO;
import com.br.syncspace.domain.paciente.Paciente;
import com.br.syncspace.domain.paciente.PacienteRepository;
import com.br.syncspace.domain.paciente.dto.CadastroPacienteRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.PerfilJaCadastradoException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {
    private final UsuarioRepository usuarioRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public CadastroService(UsuarioRepository usuarioRepository, MedicoRepository medicoRepository,
                           PacienteRepository pacienteRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Usuario cadastrarMedico(CadastroMedicoRequestDTO dto) {
        if (medicoRepository.existsByCrm(dto.crm())) {
            throw new PerfilJaCadastradoException("Já existe um médico cadastrado com este CRM.");
        }
        Usuario usuario = criarUsuario(dto.email(), dto.password(), dto.nome(), UserRole.MEDICO);
        medicoRepository.save(Medico.builder().crm(dto.crm()).especialidade(dto.especialidade()).usuario(usuario).build());
        return usuario;
    }

    @Transactional
    public Usuario cadastrarPaciente(CadastroPacienteRequestDTO dto) {
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            throw new PerfilJaCadastradoException("Já existe um paciente cadastrado com este CPF.");
        }
        Usuario usuario = criarUsuario(dto.email(), dto.password(), dto.nome(), UserRole.PACIENTE);
        pacienteRepository.save(Paciente.builder().cpf(dto.cpf()).dataNascimento(dto.dataNascimento()).usuario(usuario).build());
        return usuario;
    }

    private Usuario criarUsuario(String email, String password, String nome, UserRole role) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailJaCadastradoException("Já existe um usuário cadastrado com este e-mail.");
        }
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setNome(nome);
        usuario.setRole(role);
        return usuarioRepository.save(usuario);
    }
}
