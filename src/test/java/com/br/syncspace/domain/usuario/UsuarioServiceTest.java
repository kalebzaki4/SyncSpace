package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.usuario.dto.UsuarioRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.SenhaInvalidaException;
import com.br.syncspace.infra.exception.UsuarioNaoEncontradoException;
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

    @Test
    void findById_DeveRetornarUsuario_QuandoIdExistir() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        Usuario resultado = usuarioService.findById(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("João", resultado.getNome());
        verify(usuarioRepository, times(1)).findById(1L);
    }

    @Test
    void findById_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.findById(99L));
        verify(usuarioRepository, times(1)).findById(99L);
    }

    @Test
    void atualizarUsuario_DeveAtualizarSenha_QuandoSenhaNovaForValidaEDiferente() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "email@email.com",
                "NovaSenha@123",
                "João"
        );

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setId(1L);
        usuarioDoBanco.setEmail("email@email.com");
        usuarioDoBanco.setPassword("senhaAntigaCodificada");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioDoBanco));
        when(passwordEncoder.matches("NovaSenha@123", "senhaAntigaCodificada")).thenReturn(false);
        when(passwordEncoder.encode("NovaSenha@123")).thenReturn("novaSenhaCodificada");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioDoBanco);

        Usuario resultado = usuarioService.atualizarUsuario(usuarioLogado, requestDTO);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).matches("NovaSenha@123", "senhaAntigaCodificada");
        verify(passwordEncoder, times(1)).encode("NovaSenha@123");
        verify(usuarioRepository, times(1)).save(usuarioDoBanco);
    }

    @Test
    void atualizarUsuario_NaoDeveCodificarSenhaNovamente_QuandoSenhaNovaForIgualAAtual() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(1L);

        UsuarioRequestDTO requestDTO = new UsuarioRequestDTO(
                "email@email.com",
                "SenhaIgual@123",
                "João"
        );

        Usuario usuarioDoBanco = new Usuario();
        usuarioDoBanco.setId(1L);
        usuarioDoBanco.setEmail("email@email.com");
        usuarioDoBanco.setPassword("senhaCodificadaIgual");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioDoBanco));
        when(passwordEncoder.matches("SenhaIgual@123", "senhaCodificadaIgual")).thenReturn(true);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioDoBanco);

        Usuario resultado = usuarioService.atualizarUsuario(usuarioLogado, requestDTO);

        assertNotNull(resultado);
        verify(passwordEncoder, times(1)).matches("SenhaIgual@123", "senhaCodificadaIgual");
        verify(passwordEncoder, never()).encode(anyString());
        verify(usuarioRepository, times(1)).save(usuarioDoBanco);
    }
}