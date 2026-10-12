package com.syncspace.api.model;

import com.syncspace.api.enums.Especialidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "medicos")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Medico {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Especialidade especialidade;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String crm;

}