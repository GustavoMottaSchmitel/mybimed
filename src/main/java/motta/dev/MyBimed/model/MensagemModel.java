package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "mensagens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemModel {

    @Id
    private String id;

    @DBRef(lazy = true)
    @Field(name = "chat_id")
    private ChatModel chat;

    @DBRef(lazy = true)
    @Field(name = "cliente_id")
    private ClienteModel cliente;

    @DBRef(lazy = true)
    @Field(name = "remetente_id")
    private UserModel remetente;

    @Field(name = "conteudo")
    private String conteudo;

    @Field(name = "tipo")
    private TipoMensagem tipo;

    @Field(name = "url_arquivo")
    private String urlArquivo;

    @Field(name = "status")
    @Builder.Default
    private StatusMensagem status = StatusMensagem.ENVIADO;

    @Field(name = "entrada")
    private boolean entrada;

    @CreatedDate
    @Field(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @Field(name = "ativo")
    @Builder.Default
    private boolean ativo = true;

    @Field(name = "lido_em")
    private LocalDateTime lidoEm;

    @Field(name = "entregue_em")
    private LocalDateTime entregueEm;

    @Version
    private Long version; // Controle de concorrência

    // Método para gerar ID
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    public boolean isMensagemValida() {
        return this.ativo && this.status != StatusMensagem.ERRO;
    }

    public void marcarComoLido() {
        this.status = StatusMensagem.LIDO;
        this.lidoEm = LocalDateTime.now();
    }
}