package com.syncspace.api.service;

import com.syncspace.api.dto.DadosAtualizacaoUsuario;
import com.syncspace.api.dto.UsuarioResponseDTO;
import com.syncspace.api.exception.UsuarioJaCadastradoException;
import com.syncspace.api.exception.UsuarioNaoEncontradoException;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // buscar usuário específico
    public Usuario findUsuarioById(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado"));
    }

    // listar todos os usuários
    public List<UsuarioResponseDTO> findAllUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuario -> new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNome(),
                        usuario.getEmail()
                ))
                .toList();
    }

    // criar usuário
    @Transactional
    public Usuario createUsuario(Usuario usuario) {
        Optional<Usuario> optionalUsuario =
                usuarioRepository.findByEmail(usuario.getEmail());

        if (optionalUsuario.isPresent()) {
            throw new UsuarioJaCadastradoException("Usuário com email " + usuario.getEmail() + " já cadastrado");
        }

        String senhaCriptografada =
                passwordEncoder.encode(usuario.getSenha());

        usuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    // atualizar usuário
    @Transactional
    public Usuario updateUsuario(Long id, DadosAtualizacaoUsuario dados) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado"));

        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));

        return usuarioRepository.save(usuario);
    }

    // deletar usuário
    public void deleteUsuario(Long id) {
        Usuario usuarioDeletado = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário com ID " + id + " não encontrado"));

        usuarioRepository.delete(usuarioDeletado);
    }
}