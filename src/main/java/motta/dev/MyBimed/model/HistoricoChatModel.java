package motta.dev.MyBimed.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "historico_chat")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoChatModel {

    @Id
    private UUID id;

    // Referência para o chat
    @DBRef
    private ChatModel chat;

    private String descricao;

    private String acao; // "Trocou responsável", "Status alterado", etc.

    // Referência para o usuário que realizou a ação
    @DBRef
    private UserModel usuarioQuRealizou;

    private LocalDateTime realizadoEm;
}
