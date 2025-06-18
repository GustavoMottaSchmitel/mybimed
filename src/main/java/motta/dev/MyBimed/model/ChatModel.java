package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import motta.dev.MyBimed.enums.Chat;
import motta.dev.MyBimed.enums.StatusChat;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Chat chatTipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusChat status;

    @ManyToOne
    @JoinColumn(name = "responsavel_id", nullable = false)
    private UserModel responsavel;

    // Relacionamento de muitos-para-muitos entre Chat e User (Participantes do Chat)
    @ManyToMany
    @JoinTable(
            name = "chat_participantes", // Nome da tabela que vai guardar os participantes
            joinColumns = @JoinColumn(name = "chat_id"), // Vai guardar o id do chhat
            inverseJoinColumns = @JoinColumn(name = "usuario_id") // E O ID DO USUARIO
    )
    private List<UserModel> participantes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}
