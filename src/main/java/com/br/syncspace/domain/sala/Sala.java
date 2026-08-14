package com.br.syncspace.domain.sala;

import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.SalaInvalidaException;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Sala {

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

    public void validarCapacidadeInicialDeveSerNumeroPositivo() {
        if (this.capacidadeInicial == null || this.capacidadeInicial <= 0) {
            throw new CapacidadeExcedidaException("A capacidade inicial deve ser positiva ou maior que 0");
        }
    }

    public void validarDisponibilidadeParaReserva() {
        if (this.status != SalaStatus.ATIVA) {
            throw new SalaInvalidaException("A sala não está ativa ou esta em uso no momento.");
        }
    }

    public void setCapacidadeInicial(Integer capacidadeInicial) {
        this.capacidadeInicial = capacidadeInicial;
        validarCapacidadeInicialDeveSerNumeroPositivo();
    }
}
