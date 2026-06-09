package com.syncspace.api.service;

import com.syncspace.api.exception.UsuarioNaoEncontradoException;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    @Test
    @DisplayName("Deve retornar UserDetails quando usuário for encontrado")
    void deveRetornarUserDetails() {
        Usuario usuario = new Usuario();
        usuario.setEmail("teste@syncspace.com");
        when(usuarioRepository.findByEmail("teste@syncspace.com")).thenReturn(Optional.of(usuario));

        UserDetails resultado = autenticacaoService.loadUserByUsername("teste@syncspace.com");

        Assertions.assertNotNull(resultado);
        verify(usuarioRepository, times(1)).findByEmail("teste@syncspace.com");
    }

    @Test
    @DisplayName("Deve lançar UsuarioNaoEncontradoException se email não existir")
    void deveLancarExcecaoAoNaoEncontrarUsuario() {
        when(usuarioRepository.findByEmail("inexistente")).thenReturn(Optional.empty());

        Assertions.assertThrows(UsuarioNaoEncontradoException.class, () ->
                autenticacaoService.loadUserByUsername("inexistente")
        );
    }
}