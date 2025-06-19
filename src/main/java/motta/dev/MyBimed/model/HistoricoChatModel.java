package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "historico_chats")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoChatModel {

    @Id
    private String id;

    @DBRef(lazy = true)
    @Field(name = "chat_id")
    private ChatModel chat;

    @Field(name = "descricao")
    private String descricao;

    @Field(name = "acao")
    private String acao;

    @DBRef(lazy = true)
    @Field(name = "usuario_id")
    private UserModel usuario;

    @CreatedDate
    @Field(name = "realizado_em")
    private LocalDateTime realizadoEm;

    @Version
    private Long version;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    // Factory method para ações comuns
    public static HistoricoChatModel criarAcao(ChatModel chat, UserModel usuario, String acao) {
        return HistoricoChatModel.builder()
                .chat(chat)
                .usuario(usuario)
                .acao(acao)
                .descricao(acao + " por " + usuario.getNome())
                .build();
    }
}