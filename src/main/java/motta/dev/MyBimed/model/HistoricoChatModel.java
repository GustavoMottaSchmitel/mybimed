package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "historico_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoChatModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatModel chat;

    private String descricao;

    @Column(nullable = false)
    private String acao; // "Trocou responsável", "Status alterado", etc.

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private UserModel usuarioQuRealizou;

    @Column(nullable = false)
    private LocalDateTime realizadoEm;
}
