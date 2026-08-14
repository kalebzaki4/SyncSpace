package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.SenhaInvalidaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void loadUserByUsername_DeveRetornarUserDetails_QuandoEmailExistir() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@email.com");
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(usuario));

        UserDetails resultado = usuarioService.loadUserByUsername("teste@email.com");

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findByEmail("teste@email.com");
    }

    @Test
    void loadUserByUsername_DeveLancarExcecao_QuandoEmailNaoExistir() {
        when(usuarioRepository.findByEmail("teste@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioService.loadUserByUsername("teste@email.com"));
    }

    @Test
    void getAllUsuarios_DeveRetornarLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario()));

        List<Usuario> resultado = usuarioService.getAllUsuarios();

        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void criarUsuario_DeveSalvar_QuandoDadosForemValidos() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao@email.com", "Senha@123", "João");
        Usuario usuarioSalvo = new Usuario();

        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Senha@123")).thenReturn("senhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

        Usuario resultado = usuarioService.criarUsuario(requestDTO);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(passwordEncoder, times(1)).encode("Senha@123");
    }

    @Test
    void criarUsuario_DeveLancarExcecao_QuandoEmailJaExistir() {
        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("joao@email.com", "Senha@123", "João");
        when(usuarioRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> usuarioService.criarUsuario(requestDTO));

        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void atualizarUsuario_DeveAtualizarSemAlterarSenha_QuandoSenhaForNula() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);
        usuarioLogado.setEmail("antigo@email.com");

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("antigo@email.com", null, "João Novo");

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setId(1L);
        usuarioDoBanco.setEmail("antigo@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioDoBanco));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioDoBanco);

        Usuario resultado = usuarioService.atualizarUsuario(usuarioLogado, requestDTO);

        assertNotNull(resultado);
        assertEquals("João Novo", usuarioDoBanco.getNome());
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(usuarioDoBanco);
    }

    @Test
    void atualizarUsuario_DeveLancarExcecao_QuandoNovoEmailJaEstiverEmUso() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("novo@email.com", "Senha@123", "João Novo");

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setId(1L);
        usuarioDoBanco.setEmail("antigo@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioDoBanco));
        when(usuarioRepository.existsByEmail("novo@email.com")).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> usuarioService.atualizarUsuario(usuarioLogado, requestDTO));
    }

    @Test
    void atualizarUsuario_DeveLancarExcecao_QuandoSenhaNaoRespeitarRegex() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO("email@email.com", "senhafraca", "João");

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setId(1L);
        usuarioDoBanco.setEmail("email@email.com");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioDoBanco));

        assertThrows(SenhaInvalidaException.class, () -> usuarioService.atualizarUsuario(usuarioLogado, requestDTO));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deletarUsuario_DeveDeletar_QuandoUsuarioExistir() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        usuarioService.deletarUsuario(usuario);

        verify(usuarioRepository, times(1)).delete(usuario);
    }
}