package com.syncspace.api.model;

import com.syncspace.api.enums.Especialidade;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medico_especialidades", joinColumns = @JoinColumn(name = "medico_id", referencedColumnName = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "especialidade", nullable = false)
    @NotEmpty
    @Builder.Default
    private Set<Especialidade> especialidades = new LinkedHashSet<>();

    @Column(nullable = false, unique = true)
    @NotBlank
    private String crm;

}