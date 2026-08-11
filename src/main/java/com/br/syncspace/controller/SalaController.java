package com.br.syncspace.controller;

import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.sala.SalaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/salas")
public class SalaController {
    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @GetMapping
    public ResponseEntity<List<Sala>> listarSalas() {
        List<Sala> salas = salaService.listarSalas();
        return ResponseEntity.ok(salas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sala> verSala(@PathVariable Long id) {
        Sala sala = salaService.verSala(id);
        return ResponseEntity.ok(sala);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sala> criarSala(@RequestBody Sala sala, UriComponentsBuilder uriBuilder) {
        Sala novaSala = salaService.criarSala(sala);
        URI uri = uriBuilder.path("/salas/{id}").buildAndExpand(novaSala.getId()).toUri();
        return ResponseEntity.created(uri).body(novaSala);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Sala> atualizarSala(@PathVariable Long id, @RequestBody Sala sala) {
        sala.setId(id);
        Sala salaAtualizada = salaService.atualizarSala(sala);
        return ResponseEntity.ok(salaAtualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletarSala(@PathVariable Long id) {
        salaService.deletarSala(id);
        return ResponseEntity.noContent().build();
    }
}

