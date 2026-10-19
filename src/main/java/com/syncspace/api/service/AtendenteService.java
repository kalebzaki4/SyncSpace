package com.syncspace.api.service;

import com.syncspace.api.dto.AtendenteRequestDTO;
import com.syncspace.api.dto.AtendenteResponseDTO;
import com.syncspace.api.enums.UserRole;
import com.syncspace.api.exception.AtendenteJaExisteException;
import com.syncspace.api.exception.AtendenteNaoEncontradoException;
import com.syncspace.api.exception.UsuarioNaoEncontradoException;
import com.syncspace.api.model.Atendente;
import com.syncspace.api.model.Usuario;
import com.syncspace.api.repository.AtendenteRepository;
import com.syncspace.api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AtendenteService {
    private final AtendenteRepository atendenteRepository;
    private final UsuarioRepository usuarioRepository;

    public AtendenteService(AtendenteRepository atendenteRepository, UsuarioRepository usuarioRepository) {
        this.atendenteRepository = atendenteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<AtendenteResponseDTO> findAllAtendentes(Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN && usuario.getRole() != UserRole.ATENDENTE) {
            throw new AccessDeniedException("Acesso restrito a atendentes e administradores.");
        }
        return atendenteRepository.findAll().stream().map(AtendenteResponseDTO::new).toList();
    }

    public AtendenteResponseDTO findById(Long id, Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN && usuario.getRole() != UserRole.ATENDENTE) {
            throw new AccessDeniedException("Acesso restrito a atendentes e administradores.");
        }
        Atendente atendente = atendenteRepository.findById(id)
                .orElseThrow(() -> new AtendenteNaoEncontradoException("Atendente não encontrado"));
        return new AtendenteResponseDTO(atendente);
    }

    @Transactional
    public AtendenteResponseDTO createAtendente(AtendenteRequestDTO atendenteRequestDTO, Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN && usuario.getRole() != UserRole.ATENDENTE) {
            throw new AccessDeniedException("Acesso restrito a atendentes e administradores.");
        }

        Usuario usuarioDoAtendente = usuario;
        if (atendenteRequestDTO.usuarioId() != null) {
            if (usuario.getRole() != UserRole.ADMIN) {
                throw new AccessDeniedException("Somente um administrador pode cadastrar o perfil de outro atendente.");
            }
            usuarioDoAtendente = usuarioRepository.findById(atendenteRequestDTO.usuarioId())
                    .orElseThrow(() -> new UsuarioNaoEncontradoException());
            if (usuarioDoAtendente.getRole() != UserRole.ATENDENTE) {
                throw new AccessDeniedException("O usuário informado não possui perfil de atendente.");
            }
        } else if (usuario.getRole() == UserRole.ADMIN) {
            throw new AccessDeniedException("Informe usuarioId para cadastrar o perfil de outro atendente.");
        }

        if (atendenteRepository.existsByUsuario_Id(usuarioDoAtendente.getId())) {
            throw new AtendenteJaExisteException("O usuário informado já possui um cadastro de atendente.");
        }

        Atendente atendente = Atendente.builder()
                .usuario(usuarioDoAtendente)
                .setor(atendenteRequestDTO.setor())
                .build();

        Atendente atendenteSalvo = atendenteRepository.save(atendente);
        return new AtendenteResponseDTO(atendenteSalvo);
    }

    @Transactional
    public AtendenteResponseDTO updateAtendente(Long id, AtendenteRequestDTO request, Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        Atendente atendente = atendenteRepository.findById(id)
                .orElseThrow(() -> new AtendenteNaoEncontradoException("Atendente não encontrado"));
        if (usuario.getRole() != UserRole.ADMIN
                && !atendente.getUsuario().getId().equals(usuario.getId())) {
            throw new AccessDeniedException("Somente o próprio atendente ou um administrador pode atualizar este perfil.");
        }
        atendente.setSetor(request.setor());
        return new AtendenteResponseDTO(atendente);
    }

    @Transactional
    public void deleteAtendente(Long id, Usuario usuario) {
        if (usuario == null) {
            throw new AccessDeniedException("Autenticação necessária.");
        }
        if (usuario.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Somente um administrador pode remover atendentes.");
        }
        Atendente atendente = atendenteRepository.findById(id)
                .orElseThrow(() -> new AtendenteNaoEncontradoException("Atendente não encontrado"));
        atendenteRepository.delete(atendente);
    }

}