package com.syncspace.api.controller;

import com.syncspace.api.dto.DadosCriacaoSala;
import com.syncspace.api.dto.DadosSala;
import com.syncspace.api.model.Sala;
import com.syncspace.api.service.SalaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    // ver todos as salas
    @GetMapping
    public ResponseEntity<List<DadosSala>> getAllSalas() {
        List<Sala> salas = salaService.findAllSalas();
        List<DadosSala> dadosSalas = salas.stream().map(DadosSala::new).toList();
        return ResponseEntity.ok(dadosSalas);
    }

    // ver sala específica
    @GetMapping("/{id}")
    public ResponseEntity<DadosSala> getSalaById(@PathVariable Long id) {
        Sala sala = salaService.findSalaById(id);
        return ResponseEntity.ok(new DadosSala(sala));
    }

    // criar sala
    @PostMapping
    public ResponseEntity<DadosSala> createSala(@RequestBody @Valid DadosCriacaoSala dadosCadastro, UriComponentsBuilder uriBuilder) {
        Sala novaSala = salaService.createSala(dadosCadastro);

        var uri = uriBuilder.path("/salas/{id}").buildAndExpand(novaSala.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosSala(novaSala));
    }

    // atualizar sala
    @PutMapping("/{id}")
    public ResponseEntity<DadosSala> updateSala(@PathVariable Long id, @RequestBody @Valid DadosCriacaoSala dadosAtualizacao) {
        Sala updatedSala = salaService.updateSala(id, dadosAtualizacao);
        return updatedSala != null ? ResponseEntity.ok(new DadosSala(updatedSala)) : ResponseEntity.notFound().build();
    }

    // deletar sala
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSala(@PathVariable Long id) {
        salaService.deleteSala(id);
        return ResponseEntity.noContent().build();
    }
}
