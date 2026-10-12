package com.syncspace.api.model;

import com.syncspace.api.enums.Setores;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "atendentes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Atendente {

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
    private Setores setor;

}