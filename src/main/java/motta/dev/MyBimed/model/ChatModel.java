package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import motta.dev.MyBimed.enums.Chat;
import motta.dev.MyBimed.enums.StatusChat;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chats")  // Nome da coleção no plural
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatModel {

    @Id
    private String id;  // Usando String para melhor compatibilidade com MongoDB

    @Field(name = "nome")
    private String nome;

    @Field(name = "tipo")
    private Chat tipo;

    @Field(name = "status")
    private StatusChat status;

    @DBRef(lazy = true)  // Referência lazy para melhor performance
    @Field(name = "responsavel_id")
    private UserModel responsavel;

    @DBRef(lazy = true)
    @Field(name = "participantes")
    private List<UserModel> participantes;

    @CreatedDate
    @Field(name = "criado_em")
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Field(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    private Long version;  // Para controle de concorrência

    // Método para gerar ID automaticamente
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    // Métodos de negócio
    public boolean isAtivo() {
        return this.status == StatusChat.ATIVO;
    }

    public boolean hasParticipante(UserModel usuario) {
        return this.participantes.stream()
                .anyMatch(p -> p.getId().equals(usuario.getId()));
    }
}