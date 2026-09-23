package com.br.syncspace.domain.medico;

import com.br.syncspace.domain.usuario.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "medicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
public class Medico {

    @Id
    private Long id;

    @NotBlank(message = "A especialidade é obrigatória.")
    @Column(nullable = false)
    private String especialidade;

    @NotBlank(message = "O CRM é obrigatório.")
    @Column(nullable = false, unique = true)
    private String crm;

    @NotNull(message = "O usuário é obrigatório.")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id")
    private Usuario usuario;
}