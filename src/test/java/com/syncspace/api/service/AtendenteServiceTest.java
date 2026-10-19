package com.syncspace.api.service;

import com.syncspace.api.dto.AtendenteRequestDTO;
import com.syncspace.api.enums.Setores;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.model.Atendente;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.AtendenteRepository;
import com.syncspace.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AtendenteServiceTest {

    @Mock
    private AtendenteRepository atendenteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AtendenteService atendenteService;

    @Test
    void adminCanCreateProfileForExistingAtendenteAccount() {
        Usuario admin = usuario(1L, UserRole.ADMIN);
        Usuario novoAtendente = usuario(2L, UserRole.ATENDENTE);
        when(usuarioRepository.findById(novoAtendente.getId())).thenReturn(Optional.of(novoAtendente));
        when(atendenteRepository.existsByUsuario_Id(novoAtendente.getId())).thenReturn(false);
        when(atendenteRepository.save(any(Atendente.class))).thenAnswer(invocation -> invocation.getArgument(0));

        atendenteService.createAtendente(
                new AtendenteRequestDTO(novoAtendente.getId(), Setores.ATENDIMENTO),
                admin
        );

        verify(atendenteRepository).save(org.mockito.ArgumentMatchers.argThat(
                atendente -> atendente.getUsuario().getId().equals(novoAtendente.getId())
        ));
    }

    @Test
    void atendenteCannotCreateProfileForAnotherUser() {
        Usuario atendente = usuario(1L, UserRole.ATENDENTE);

        assertThrows(
                AccessDeniedException.class,
                () -> atendenteService.createAtendente(
                        new AtendenteRequestDTO(2L, Setores.ATENDIMENTO),
                        atendente
                )
        );
    }

    private Usuario usuario(Long id, UserRole role) {
        return Usuario.builder()
                .id(id)
                .role(role)
                .build();
    }
}
