package com.syncspace.api.controller;

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
    public ResponseEntity<List<Sala>> getAllSalas() {
        List<Sala> salas = salaService.findAllSalas();
        return ResponseEntity.ok(salas);
    }

    // ver sala específica
    @GetMapping("/{id}")
    public ResponseEntity<Sala> getSalaById(@PathVariable Long id) {
        Sala sala = salaService.findSalaById(id);
        return ResponseEntity.ok(sala);
    }

    // criar sala
    @PostMapping
    public ResponseEntity<Sala> createSala(@RequestBody @Valid DadosSala dadosCadastro, UriComponentsBuilder uriBuilder) {
        var sala = new Sala();
        sala.setNome(dadosCadastro.nome());
        sala.setDescricao(dadosCadastro.descricao());
        sala.setQuantidade(dadosCadastro.quantidade());
        sala.setTempoExpiracao(dadosCadastro.tempoExpiracao());
        Sala novaSala = salaService.createSala(sala);

        var uri = uriBuilder.path("/salas/{id}").buildAndExpand(novaSala.getId()).toUri();
        return ResponseEntity.created(uri).body(novaSala);
    }

    // atualizar sala
    @PutMapping("/{id}")
    public ResponseEntity<Sala> updateSala(@PathVariable Long id, @RequestBody @Valid DadosSala dadosAtualizacao) {
        Sala updatedSala = salaService.updateSala(id, dadosAtualizacao);
        return updatedSala != null ? ResponseEntity.ok(updatedSala) : ResponseEntity.notFound().build();
    }

    // deletar sala
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSala(@PathVariable Long id) {
        salaService.deleteSala(id);
        return ResponseEntity.noContent().build();
    }
}
