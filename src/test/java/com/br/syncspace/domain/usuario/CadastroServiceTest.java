package com.br.syncspace.domain.usuario;

import com.br.syncspace.domain.medico.Medico;
import com.br.syncspace.domain.medico.MedicoRepository;
import com.br.syncspace.domain.medico.dto.CadastroMedicoRequestDTO;
import com.br.syncspace.domain.paciente.Paciente;
import com.br.syncspace.domain.paciente.PacienteRepository;
import com.br.syncspace.domain.paciente.dto.CadastroPacienteRequestDTO;
import com.br.syncspace.infra.exception.EmailJaCadastradoException;
import com.br.syncspace.infra.exception.PerfilJaCadastradoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastroServiceTest {
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MedicoRepository medicoRepository;
    @Mock private PacienteRepository pacienteRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private CadastroService cadastroService;

    @Test
    void cadastrarMedico_DeveCriarUsuarioEPerfilComMesmoId() {
        var dto = new CadastroMedicoRequestDTO("medico@email.com", "Senha@123", "Dra. Ana", "CRM-1", "Cardiologia");
        Usuario usuario = new Usuario(); usuario.setId(10L);
        when(medicoRepository.existsByCrm(dto.crm())).thenReturn(false);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hash");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        cadastroService.cadastrarMedico(dto);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Medico> perfil = ArgumentCaptor.forClass(Medico.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        verify(medicoRepository).save(perfil.capture());
        assertEquals(UserRole.MEDICO, usuarioCaptor.getValue().getRole());
        assertEquals(usuario, perfil.getValue().getUsuario());
        assertEquals("CRM-1", perfil.getValue().getCrm());
    }

    @Test
    void cadastrarPaciente_DeveCriarUsuarioEPerfilComMesmoId() {
        var dto = new CadastroPacienteRequestDTO("paciente@email.com", "Senha@123", "João", "12345678900", LocalDate.of(1990, 1, 1));
        Usuario usuario = new Usuario(); usuario.setId(11L);
        when(pacienteRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(false);
        when(passwordEncoder.encode(dto.password())).thenReturn("hash");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        cadastroService.cadastrarPaciente(dto);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        ArgumentCaptor<Paciente> perfil = ArgumentCaptor.forClass(Paciente.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        verify(pacienteRepository).save(perfil.capture());
        assertEquals(UserRole.PACIENTE, usuarioCaptor.getValue().getRole());
        assertEquals(usuario, perfil.getValue().getUsuario());
        assertEquals("12345678900", perfil.getValue().getCpf());
    }

    @Test
    void cadastrarMedico_DeveRejeitarCrmDuplicadoAntesDeCriarUsuario() {
        var dto = new CadastroMedicoRequestDTO("medico@email.com", "Senha@123", "Dra. Ana", "CRM-1", "Cardiologia");
        when(medicoRepository.existsByCrm(dto.crm())).thenReturn(true);

        assertThrows(PerfilJaCadastradoException.class, () -> cadastroService.cadastrarMedico(dto));
        verifyNoInteractions(usuarioRepository, passwordEncoder);
    }

    @Test
    void cadastrarPaciente_DeveRejeitarEmailDuplicado() {
        var dto = new CadastroPacienteRequestDTO("paciente@email.com", "Senha@123", "João", "12345678900", LocalDate.of(1990, 1, 1));
        when(pacienteRepository.existsByCpf(dto.cpf())).thenReturn(false);
        when(usuarioRepository.existsByEmail(dto.email())).thenReturn(true);

        assertThrows(EmailJaCadastradoException.class, () -> cadastroService.cadastrarPaciente(dto));
        verify(pacienteRepository, never()).save(any());
    }
}
