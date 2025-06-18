package motta.dev.MyBimed.model;

import lombok.*;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "mensagem")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensagemModel {

    @Id
    private UUID id;

    // Referência para o chat
    @DBRef
    private ChatModel chat;

    // Referência para o cliente
    @DBRef
    private ClienteModel cliente;

    // Referência para o remetente
    @DBRef
    private UserModel remetente;

    private String conteudo;

    private TipoMensagem tipoMensagem;

    private String urlArquivo;

    private StatusMensagem statusMensagem = StatusMensagem.ENVIADO;

    private boolean mensagemDeEntrada;

    @CreationTimestamp
    private LocalDateTime enviadoEm;

    private boolean ativo = true;

    private LocalDateTime lidoEm;
    private LocalDateTime entregueEm;
}