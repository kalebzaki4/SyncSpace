package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.usuario.Usuario;
import com.br.syncspace.infra.exception.CapacidadeExcedidaException;
import com.br.syncspace.infra.exception.HoraErradaException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeDoPaciente;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    @Column(nullable = false)
    private Integer quantidadePessoas;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    public void validarIntervaloDatas() {
        if (this.dataHoraInicio == null || this.dataHoraFim == null) {
            throw new HoraErradaException("As datas de início e fim da reserva são obrigatórias.");
        }

        if (!this.dataHoraInicio.isBefore(this.dataHoraFim)) {
            throw new HoraErradaException("A data de início da reserva deve ser estritamente anterior à data de término.");
        }
    }

    public void validarCapacidadePessoas() {
        if (this.quantidadePessoas == null || this.quantidadePessoas <= 0) {
            throw new IllegalArgumentException("A quantidade de pessoas deve ser um número positivo maior que zero.");
        }

        if (this.sala != null && this.sala.getCapacidadeInicial() != null) {
            if (this.quantidadePessoas > this.sala.getCapacidadeInicial()) {
                throw new CapacidadeExcedidaException("A quantidade de pessoas excede a capacidade máxima suportada pela sala.");
            }
        }
    }

    public void setDataHoraInicio(LocalDateTime dataHoraInicio) {
        this.dataHoraInicio = dataHoraInicio;
        if (this.dataHoraFim != null) {
            validarIntervaloDatas();
        }
    }

    public void setDataHoraFim(LocalDateTime dataHoraFim) {
        this.dataHoraFim = dataHoraFim;
        if (this.dataHoraInicio != null) {
            validarIntervaloDatas();
        }
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
        validarCapacidadePessoas();
    }
}