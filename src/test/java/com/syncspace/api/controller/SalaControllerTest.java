package com.syncspace.api.controller;

import com.syncspace.api.dto.DadosCriacaoSala;
import com.syncspace.api.dto.DadosSala;
import com.syncspace.api.model.Sala;
import com.syncspace.api.service.SalaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaControllerTest {

    @Mock
    private SalaService salaService;

    @InjectMocks
    private SalaController salaController;

    @Test
    @DisplayName("Deve listar todas as salas")
    void deveListarSalas() {
        Sala sala = new Sala();
        when(salaService.findAllSalas()).thenReturn(List.of(sala));

        ResponseEntity<List<DadosSala>> resposta = salaController.getAllSalas();

        Assertions.assertEquals(HttpStatus.OK, resposta.getStatusCode());
        Assertions.assertEquals(1, resposta.getBody().size());
    }

    @Test
    @DisplayName("Deve criar sala e retornar 201 Created")
    void deveCriarSala() {
        DadosCriacaoSala dados = new DadosCriacaoSala("Sala 1", "Desc", 5, 30);
        Sala salaSalva = new Sala();
        salaSalva.setId(1L);

        when(salaService.createSala(dados)).thenReturn(salaSalva);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<DadosSala> resposta = salaController.createSala(dados, uriBuilder);

        Assertions.assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        Assertions.assertNotNull(resposta.getHeaders().getLocation());
    }
}