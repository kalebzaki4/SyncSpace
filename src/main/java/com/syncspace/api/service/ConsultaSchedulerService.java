package com.syncspace.api.service;

import com.syncspace.api.enums.StatusConsulta;
import com.syncspace.api.model.Consulta;
import com.syncspace.api.repository.ConsultaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ConsultaSchedulerService {

    private final ConsultaRepository consultaRepository;

    public ConsultaSchedulerService(ConsultaRepository consultaRepository) {
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 300000)
    public void verificarConsultarExpiradas() {
        List<Consulta> expiradas = consultaRepository.findByStatusReservaInAndDataHoraFimBefore(List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA), new Date(System.currentTimeMillis() - 600000));
        for (Consulta consulta : expiradas) {
            consulta.setStatusConsulta(StatusConsulta.EXPIRADA);
        }
        consultaRepository.saveAll(expiradas);
    }
}
