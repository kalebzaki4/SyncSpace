package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.SenhaInvalidaException;
import com.br.syncspace.infra.exception.UsuarioNaoEncontradoException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));
    }

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario nao encontrado"));
    }

    public Usuario criarPaciente(UsuarioRequestDTO requestDTO) {
        return criarUsuario(requestDTO, UserRole.PACIENTE);
    }

    public Usuario criarMedico(UsuarioRequestDTO requestDTO) {
        return criarUsuario(requestDTO, UserRole.MEDICO);
    }

    private Usuario criarUsuario(UsuarioRequestDTO requestDTO, UserRole role) {
        if (usuarioRepository.existsByEmail(requestDTO.email())) {
            throw new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email.");
        }

        Usuario usuario = new Usuario();
        BeanUtils.copyProperties(requestDTO, usuario);
        usuario.setRole(role);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizarUsuario(Usuario usuarioLogado, UsuarioRequestDTO requestDTO) {
        Usuario usuarioDoBanco = findById(usuarioLogado.getId());

        if (!usuarioDoBanco.getEmail().equals(requestDTO.email()) && usuarioRepository.existsByEmail(requestDTO.email())) {
            throw new EmailJaCadastradoException("Ja existe um usuario cadastrado com este email.");
        }

        usuarioDoBanco.setEmail(requestDTO.email());
        usuarioDoBanco.setNome(requestDTO.nome());

        if (requestDTO.password() != null && !requestDTO.password().isBlank()) {
            String regexSenha = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.])[A-Za-z\\d@$!%*?&.]{8,}$";
            if (!requestDTO.password().matches(regexSenha)) {
                throw new SenhaInvalidaException("A senha deve ter no mínimo 8 caracteres e conter pelo menos uma letra maiúscula, uma minúscula, um número e um caractere especial.");
            }
            boolean senhaEhDiferente = !passwordEncoder.matches(requestDTO.password(), usuarioDoBanco.getSenha());
            if (senhaEhDiferente) {
                usuarioDoBanco.setSenha(passwordEncoder.encode(requestDTO.password()));
            }
        }

        return usuarioRepository.save(usuarioDoBanco);
    }

    // desativar usuario
    public void deletarUsuario(Usuario usuario) {
        Usuario usuarioDoBanco = findById(usuario.getId());
        if (usuarioDoBanco == null) {
            throw new UsuarioNaoEncontradoException("Usuario nao encontrado");
        }
        usuarioRepository.delete(usuarioDoBanco);
    }


}
