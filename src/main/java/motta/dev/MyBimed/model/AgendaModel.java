package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import motta.dev.MyBimed.enums.StatusAgenda;
import motta.dev.MyBimed.enums.TipoAgenda;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "agenda")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAgenda tipoAgenda;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    private String descricao;

    // Participantes da agenda (pode ser uma gravação, uma reunião, etc.
    @ManyToMany
    @JoinTable(
            name = "agenda_usuarios",
            joinColumns = @JoinColumn(name = "agemda_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UserModel> participantes;

    // Cliente relacionado (Caso for uma reunião com um médico)
    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClienteModel cliente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgenda status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

}

