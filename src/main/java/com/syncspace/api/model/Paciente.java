package com.syncspace.api.model;

import com.syncspace.api.enums.Alergias;
import com.syncspace.api.enums.CondicoesCronicas;
import com.syncspace.api.enums.TipoSanguineo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "pacientes")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Paciente {
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sanguineo", nullable = false)
    private TipoSanguineo tipoSanguineo;

    @Enumerated(EnumType.STRING)
    @Column(name = "alergias", nullable = false)
    private Alergias alergias;

    @Enumerated(EnumType.STRING)
    @Column(name = "doencas_cronicas", nullable = false)
    private CondicoesCronicas doencasCronicas;

    @Enumerated(EnumType.STRING)
    @Column(name = "medicamentos", nullable = false)
    private CondicoesCronicas medicamentos;

    @Column(name = "Contato_de_emergencia", nullable = false)
    @NotNull
    @Size(max = 100)
    private String contatoDeEmergencia;

    @Column(name = "telefone_contato_emergencia", nullable = false)
    @NotNull
    @Size(max = 15)
    private String telefoneContatoEmergencia;

    @Column(name = "prontuario", nullable = false, unique = true)
    @NotNull
    @Size(max = 20)
    private String prontuario;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> consultas;
}
