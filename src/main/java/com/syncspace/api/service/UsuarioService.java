package com.syncspace.api.service;

import com.syncspace.api.dto.UsuarioRequestDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.exception.UsuarioJaExisteException;
import com.syncspace.api.exception.UsuarioNaoEncontradoException;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findbyEmail(email).orElseThrow(UsuarioNaoEncontradoException::new);
    }

    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario findById(Long id) {
        return usuarioRepository.findById(id).orElseThrow(UsuarioNaoEncontradoException::new);
    }

    @Transactional
    public Usuario createUsuario(@Valid UsuarioRequestDTO usuario, UserRole userRole) {
        if (usuarioRepository.findbyEmail(usuario.email()).isPresent()) {
            throw new UsuarioJaExisteException("Usuário com o email informado já existe.");
        }

        Usuario newUsuario = new Usuario();
        newUsuario.builder()
                .nome(usuario.nome())
                .email(usuario.email())
                .senha(passwordEncoder.encode(usuario.senha()))
                .telefone(usuario.telefone())
                .dataNascimento(usuario.dataNascimento())
                .cpf(usuario.cpf())
                .role(userRole)
                .build();

        return usuarioRepository.save(newUsuario);
    }
}
