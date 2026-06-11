package com.syncspace.api.domain.usuario;

import com.syncspace.api.domain.usuario.dto.DadosAtualizacaoUsuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve criar usuário com senha criptografada")
    void deveCriarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@teste.com");
        usuario.setSenha("123456");

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("123456")).thenReturn("senha_cripto");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.createUsuario(usuario);

        Assertions.assertEquals("senha_cripto", resultado.getSenha());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    @DisplayName("Deve atualizar usuário existente")
    void deveAtualizarUsuario() {
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);

        DadosAtualizacaoUsuario dados = new DadosAtualizacaoUsuario("Novo Nome", "novo@teste.com", "novaSenha");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode(dados.senha())).thenReturn("nova_senha_cripto");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioExistente);

        Usuario resultado = usuarioService.updateUsuario(1L, dados);

        Assertions.assertEquals("Novo Nome", resultado.getNome());
        Assertions.assertEquals("novo@teste.com", resultado.getEmail());
    }
}
