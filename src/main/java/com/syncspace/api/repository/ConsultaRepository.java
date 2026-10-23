package com.syncspace.api.repository;

import com.syncspace.api.enums.StatusConsulta;
import com.syncspace.api.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Date;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findAllByMedico_Usuario_IdOrderByDataHoraInicioAsc(Long usuarioId);

    boolean existsBySala_Id(Long salaId);

    boolean existsByPaciente_Id(Long pacienteId);

    boolean existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
            Long salaId, Date dataHoraFim, Date dataHoraInicio
    );

    boolean existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
            Long medicoId, Date dataHoraFim, Date dataHoraInicio
    );

    boolean existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThan(
            Long pacienteId, Date dataHoraFim, Date dataHoraInicio
    );

    boolean existsBySala_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
            Long salaId, Date dataHoraFim, Date dataHoraInicio, Long id
    );

    boolean existsByMedico_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
            Long medicoId, Date dataHoraFim, Date dataHoraInicio, Long id
    );

    boolean existsByPaciente_IdAndDataHoraInicioLessThanAndDataHoraFimGreaterThanAndIdNot(
            Long pacienteId, Date dataHoraFim, Date dataHoraInicio, Long id
    );

    List<Consulta> findByStatusReservaInAndDataHoraFimBefore(List<StatusConsulta> status, Date dataAtual);
}
