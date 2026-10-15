package com.syncspace.api.repository;

import com.syncspace.api.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {
    boolean existsByCrm(String crm);

    boolean existsByCrmAndIdNot(String crm, Long id);

    boolean existsByUsuario_Id(Long usuarioId);
}
