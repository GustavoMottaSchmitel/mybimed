package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import motta.dev.MyBimed.enums.StatusProjeto;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "projetos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjetoModel {

    @Id
    private String id;

    @Field(name = "titulo")
    private String titulo;

    @Field(name = "descricao")
    private String descricao;

    @Field(name = "status")
    private StatusProjeto status;

    @DBRef(lazy = true)
    @Field(name = "cliente")
    private ClienteModel cliente;

    @DBRef(lazy = true)
    @Field(name = "equipe")
    private List<UserModel> equipe;

    @Field(name = "data_entrega")
    private LocalDateTime dataEntrega;

    @CreatedDate
    @Field(name = "criado_em")
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Field(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    private Long version;

    // Método para gerar ID
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    // Método para verificar se o projeto está ativo
    public boolean isAtivo() {
        return status == StatusProjeto.EM_ANDAMENTO ||
                status == StatusProjeto.EM_ANALISE;
    }
}