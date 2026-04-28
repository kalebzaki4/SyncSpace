package com.syncspace.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
public class Sala {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false)
    private Long tempoExpiracao;
}
