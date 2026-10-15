package com.syncspace.api.repository;

import com.syncspace.api.model.Atendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtendenteRepository extends JpaRepository<Atendente, Long> {
    boolean existsByUsuario_Id(Long usuarioId);
}
