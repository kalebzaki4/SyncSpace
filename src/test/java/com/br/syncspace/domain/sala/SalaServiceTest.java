package com.br.syncspace.domain.sala;

import com.br.syncspace.domain.sala.dto.SalaRequestDTO;
import com.br.syncspace.infra.exception.SalaNaoEncontradaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaServiceTest {

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private SalaService salaService;

    @Test
    void listarSalas_DeveRetornarListaDeSalas() {
        Sala sala = new Sala();
        when(salaRepository.findAll()).thenReturn(List.of(sala));

        List<Sala> resultado = salaService.listarSalas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(salaRepository, times(1)).findAll();
    }

    @Test
    void verSala_DeveRetornarSala_QuandoIdExistir() {
        Sala sala = new Sala();
        sala.setId(1L);
        when(salaRepository.findById(1L)).thenReturn(Optional.of(sala));

        Sala resultado = salaService.verSala(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(salaRepository, times(1)).findById(1L);
    }

    @Test
    void verSala_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SalaNaoEncontradaException.class, () -> salaService.verSala(1L));
        verify(salaRepository, times(1)).findById(1L);
    }

    @Test
    void criarSala_DeveSalvarERetornarSala() {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala A", "Descrição A", 10);
        Sala salaSalva = new Sala();
        salaSalva.setNome("Sala A");

        when(salaRepository.save(any(Sala.class))).thenReturn(salaSalva);

        Sala resultado = salaService.criarSala(requestDTO);

        assertNotNull(resultado);
        assertEquals("Sala A", resultado.getNome());
        verify(salaRepository, times(1)).save(any(Sala.class));
    }

    @Test
    void atualizarSala_DeveAtualizarERetornarSala_QuandoIdExistir() {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Atualizada", "Nova Descrição", 20);
        Sala salaDoBanco = new Sala();
        salaDoBanco.setId(1L);
        salaDoBanco.setNome("Sala Antiga");

        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaDoBanco));
        when(salaRepository.save(any(Sala.class))).thenReturn(salaDoBanco);

        Sala resultado = salaService.atualizarSala(requestDTO, 1L);

        assertNotNull(resultado);
        assertEquals("Sala Atualizada", salaDoBanco.getNome());
        verify(salaRepository, times(1)).findById(1L);
        verify(salaRepository, times(1)).save(salaDoBanco);
    }

    @Test
    void atualizarSala_DeveLancarExcecao_QuandoIdNaoExistir() {
        SalaRequestDTO requestDTO = new SalaRequestDTO("Sala Atualizada", "Nova Descrição", 20);

        when(salaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SalaNaoEncontradaException.class, () -> salaService.atualizarSala(requestDTO, 99L));

        verify(salaRepository, times(1)).findById(99L);
        verify(salaRepository, never()).save(any());
    }

    @Test
    void deletarSala_DeveDeletar_QuandoIdExistir() {
        when(salaRepository.existsById(1L)).thenReturn(true);

        salaService.deletarSala(1L);

        verify(salaRepository, times(1)).existsById(1L);
        verify(salaRepository, times(1)).deleteById(1L);
    }

    @Test
    void deletarSala_DeveLancarExcecao_QuandoIdNaoExistir() {
        when(salaRepository.existsById(1L)).thenReturn(false);

        assertThrows(SalaNaoEncontradaException.class, () -> salaService.deletarSala(1L));

        verify(salaRepository, times(1)).existsById(1L);
        verify(salaRepository, never()).deleteById(any());
    }
}