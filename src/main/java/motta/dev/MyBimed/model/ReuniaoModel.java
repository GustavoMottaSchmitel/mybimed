package motta.dev.MyBimed.model;

import lombok.*;
import motta.dev.MyBimed.enums.StatusReuniao;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "reuniao")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReuniaoModel {

    @Id
    private UUID id;

    private String titulo;

    private String descricao;

    // Relacionamento para agenda
    @DBRef
    private AgendaModel agenda;

    // Relacionamento para usuários participantes
    @DBRef
    private List<UserModel> participantes;

    private String link;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    private StatusReuniao status;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

}