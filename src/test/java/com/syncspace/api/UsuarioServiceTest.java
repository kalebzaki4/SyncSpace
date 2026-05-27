package com.syncspace.api;

import com.syncspace.api.dto.DadosAtualizacaoUsuario;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import com.syncspace.api.service.UsuarioService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
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

    @DisplayName("Deve buscar todos os usuários")
    @Test
    public void deveBuscarTodosOsUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(java.util.Collections.emptyList());

        Assertions.assertTrue(usuarioService.findAllUsuarios().isEmpty());
    }

    @DisplayName("Deve criar usuário com sucesso")
    @Test
    public void deveCriarUsuarioELancarSucesso() {
        Usuario usuario = new Usuario();
        usuario.setEmail("email");
        usuario.setSenha("senha");
        usuario.setId(1L);

        when(usuarioRepository.findByEmail("email")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha")).thenReturn("senhaCriptografada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario usuarioSalvo = usuarioService.createUsuario(usuario);

        Assertions.assertNotNull(usuarioSalvo);
        Assertions.assertEquals(1L, usuarioSalvo.getId());
    }

    @DisplayName("Deve lançar exceção ao criar usuário com email já cadastrado")
    @Test
    public void deveCriarUsuarioELancarExcecaoQuandoEmailJaExiste() {
        Usuario usuario = new Usuario();
        usuario.setEmail("email");

        when(usuarioRepository.findByEmail("email")).thenReturn(Optional.of(new Usuario()));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> {
            usuarioService.createUsuario(usuario);
        });

        Assertions.assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @DisplayName("Deve deletar usuário com sucesso quando o ID existir")
    @Test
    public void deveDeletarUsuarioComSucessoQuandoIdExistir() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deleteUsuario(1L);

        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @DisplayName("Deve lançar exceção ao tentar deletar usuário com ID inexistente")
    @Test
    public void deveLancarExcecaoAoTentarDeletarUsuarioComIdInexistente() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(RuntimeException.class, () -> {
            usuarioService.deleteUsuario(1L);
        });
    }

    @DisplayName("Deve atualizar usuário com sucesso quando o ID existir")
    @Test
    public void deveAtualizarUsuarioComSucessoQuandoIdExistir() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("email@antigo.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(passwordEncoder.encode("novaSenha123")).thenReturn("senhaCriptografada");

        // Criar DTO com os novos dados
        DadosAtualizacaoUsuario novosDados = new DadosAtualizacaoUsuario(
                "Novo Nome",
                "novoEmail@teste.com",
                "novaSenha123"
        );

        // Mock do save para retornar o usuário atualizado
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        // Act
        Usuario usuarioAtualizado = usuarioService.updateUsuario(1L, novosDados);

        // Assert
        Assertions.assertNotNull(usuarioAtualizado);
        Assertions.assertEquals("novoEmail@teste.com", usuarioAtualizado.getEmail());
        Assertions.assertEquals("Novo Nome", usuarioAtualizado.getNome());

        verify(usuarioRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).encode("novaSenha123");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }
}