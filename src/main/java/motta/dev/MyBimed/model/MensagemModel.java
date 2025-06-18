package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mensagem")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // Em qual chat essa mensagem foi enviada
    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private ChatModel chat;

    // Quem enviou essa mensagem
    @ManyToOne
    @JoinColumn(name = "remetente_id", nullable = false)
    private UserModel remetente;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMensagem tipoMensagem;

    private String urlArquivo;

    @Enumerated(EnumType.STRING)
    private StatusMensagem statusMensagem;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime enviadoEm;

    private LocalDateTime lidoEm;
    private LocalDateTime entregueEm;

}
