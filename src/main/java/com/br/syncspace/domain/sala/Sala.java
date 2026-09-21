package com.br.syncspace.domain.sala;

import com.br.syncspace.domain.reserva.Reserva;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.SalaInvalidaException;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sala implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private Integer capacidadeInicial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaStatus status;

    @OneToMany(mappedBy = "sala", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;
}
