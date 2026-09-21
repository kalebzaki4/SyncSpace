package com.br.syncspace.domain.reserva;

import com.br.syncspace.domain.sala.Sala;
import com.br.syncspace.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Reserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do paciente é obrigatório.")
    @Column(nullable = false)
    private String nomeDoPaciente;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "A data e hora de início são obrigatórias.")
    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @NotNull(message = "A data e hora do fim são obrigatórias.")
    @Column(nullable = false)
    private LocalDateTime dataHoraFim;

    @NotNull(message = "A quantidade de pessoas é obrigatória.")
    @Positive(message = "A quantidade de pessoas deve ser um número positivo maior que zero.")
    @Column(nullable = false)
    private Integer quantidadePessoas;

    @NotNull(message = "O status da reserva é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @NotNull(message = "O usuário é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "A sala é obrigatória.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;
}