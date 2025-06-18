package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import motta.dev.MyBimed.enums.ProjetoStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projeto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjetoStatus projetoStatus;

    // Cliente relacionado ao projeto
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteModel cliente;

    // Lista de usuários responsáveis pelo projeto
    @ManyToMany
    @JoinTable(
            name = "projeto_equipe",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private List<UserModel> equipeResponsavel;

    @Column(nullable = false)
    private LocalDateTime dataEntrega;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

}
