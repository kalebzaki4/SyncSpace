package com.syncspace.api;

import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import com.syncspace.api.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsuarioService usuarioService;

    @DisplayName("Deve buscar usuário por ID e dar sucesso")
    @Test
    public void deveBuscarUsuarioPorIdEDarSucesso() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario usuarioEncontrado = usuarioService.findUsuarioById(1L);

        Assertions.assertNotNull(usuarioEncontrado);
        Assertions.assertEquals(1L, usuarioEncontrado.getId());
    }

    @DisplayName("Deve buscar todos os usuarios e lançar exceção quando não encontrado")
    @Test
    public void deveBuscarTodosOsUsuariosELancarExcecaoQuandoNaoEncontrado() {
        when(usuarioRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        Assertions.assertTrue(usuarioService.findAllUsuarios().isEmpty());
    }

    @DisplayName("Deve criar usuário e lançar sucesso")
    @Test
    public void deveCriarUsuarioELancarSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setSenha("senha");
        usuario.setId(1L);

        when(usuarioRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario usuarioSalvo = usuarioService.createUsuario(usuario);
        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(1L, usuarioSalvo.getId());
    }
}
