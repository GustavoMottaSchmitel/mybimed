package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import motta.dev.MyBimed.enums.StatusReuniao;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reuniao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReuniaoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    private String descricao;

    @ManyToOne
    @JoinColumn(name = "agenda_id")
    private AgendaModel agenda;

    @ManyToMany
    @JoinTable(
            name = "reuniao_usuarios",
            joinColumns = @JoinColumn(name = "reuniao_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UserModel> participantes;

    private String link;

    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReuniao status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

}
