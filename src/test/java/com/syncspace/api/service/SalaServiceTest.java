package com.syncspace.api.service;

import com.syncspace.api.dto.DadosCriacaoSala;
import com.syncspace.api.exception.SalaJaCadastradaException;
import com.syncspace.api.model.Sala;
import com.syncspace.api.repository.SalaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    @DisplayName("Deve criar sala com sucesso")
    void deveCriarSala() {
        DadosCriacaoSala dados = new DadosCriacaoSala("Reunião X", "Desc", 10, 60);
        when(salaRepository.findByNome(dados.nome())).thenReturn(Optional.empty());
        when(salaRepository.save(any(Sala.class))).thenAnswer(i -> i.getArguments()[0]);

        Sala novaSala = salaService.createSala(dados);

        Assertions.assertEquals("Reunião X", novaSala.getNome());
        verify(salaRepository, times(1)).save(any(Sala.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar sala com nome duplicado")
    void deveLancarExcecaoSalaDuplicada() {
        DadosCriacaoSala dados = new DadosCriacaoSala("Reunião X", "Desc", 10, 60);
        when(salaRepository.findByNome(dados.nome())).thenReturn(Optional.of(new Sala()));

        Assertions.assertThrows(SalaJaCadastradaException.class, () -> salaService.createSala(dados));
    }

    @Test
    @DisplayName("Deve deletar sala existente")
    void deveDeletarSala() {
        Sala sala = new Sala();
        sala.setId(1L);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        salaService.deleteSala(1L);

        verify(salaRepository, times(1)).delete(sala);
    }
}