package com.syncspace.api.domain.sala;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SalaRepository extends JpaRepository<Sala, Long> {
    Optional<Sala> findByNome(String nome);

    @Transactional
    void deleteByTempoExpiracaoBefore(LocalDateTime horario);
}
