package com.syncspace.api.repository;

import com.syncspace.api.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    boolean existsByProntuario(String prontuario);

    boolean existsByProntuarioAndIdNot(String prontuario, Long id);

    boolean existsByUsuario_Id(Long usuarioId);
}
