package com.br.syncspace.domain.paciente;

import com.br.syncspace.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "pacientes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Paciente {

    @Id
    private Long id;

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve estar no passado.")
    @Column(nullable = false)
    private LocalDate dataNascimento;

    @NotBlank(message = "O CPF é obrigatório.")
    @Column(nullable = false, unique = true)
    private String cpf;

    @NotNull(message = "O usuário é obrigatório.")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;
}
