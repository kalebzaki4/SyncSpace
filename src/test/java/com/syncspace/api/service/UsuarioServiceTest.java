package com.syncspace.api.service;

import com.syncspace.api.dto.UsuarioRequestDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
    void atendenteCanCreateMedicoAccount() {
        UsuarioRequestDTO request = request(UserRole.MEDICO);
        Usuario atendente = usuario(UserRole.ATENDENTE);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(null);
        when(passwordEncoder.encode(request.senha())).thenReturn("senha-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario created = usuarioService.createUsuario(request, atendente);

        assertEquals(UserRole.MEDICO, created.getRole());
        assertEquals("senha-hash", created.getSenha());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void adminCanCreateAtendenteAccount() {
        UsuarioRequestDTO request = request(UserRole.ATENDENTE);
        when(usuarioRepository.findByEmail(request.email())).thenReturn(null);
        when(passwordEncoder.encode(request.senha())).thenReturn("senha-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario created = usuarioService.createUsuario(request, usuario(UserRole.ADMIN));

        assertEquals(UserRole.ATENDENTE, created.getRole());
        assertEquals("senha-hash", created.getSenha());
    }

    @Test
    void adminCannotCreateMedicoAccount() {
        assertThrows(
                AccessDeniedException.class,
                () -> usuarioService.createUsuario(request(UserRole.MEDICO), usuario(UserRole.ADMIN))
        );
    }

    @Test
    void atendenteCannotCreateAtendenteAccount() {
        assertThrows(
                AccessDeniedException.class,
                () -> usuarioService.createUsuario(request(UserRole.ATENDENTE), usuario(UserRole.ATENDENTE))
        );
    }

    private UsuarioRequestDTO request(UserRole role) {
        return new UsuarioRequestDTO(
                "Conta de Teste",
                "teste@syncspace.local",
                "SenhaSegura123",
                "11999999999",
                new Date(),
                "12345678901",
                role
        );
    }

    private Usuario usuario(UserRole role) {
        return Usuario.builder()
                .id(1L)
                .role(role)
                .build();
    }
}
