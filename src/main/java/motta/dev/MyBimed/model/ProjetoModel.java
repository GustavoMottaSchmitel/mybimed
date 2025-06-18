package motta.dev.MyBimed.model;

import lombok.*;
import motta.dev.MyBimed.enums.StatusProjeto;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "projeto")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoModel {

    @Id
    private UUID id;

    private String titulo;

    private String descricao;

    private StatusProjeto statusProjeto;

    // Cliente relacionado ao projeto
    @DBRef
    private ClienteModel cliente;

    // Lista de usuários responsáveis pelo projeto
    @DBRef
    private List<UserModel> equipeResponsavel;

    private LocalDateTime dataEntrega;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}