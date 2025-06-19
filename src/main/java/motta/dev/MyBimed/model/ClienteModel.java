package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import motta.dev.MyBimed.enums.Status;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "clientes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteModel {

    @Id
    private String id;

    @Field(name = "nome")
    private String nome;

    @Field(name = "especialidade")
    private String especialidade;

    @Field(name = "email")
    private String email;

    @Field(name = "telefone")
    private String telefone;

    @Field(name = "whatsapp")
    private String whatsapp;

    @Field(name = "status")
    private Status status;

    @CreatedDate
    @Field(name = "criado_em")
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Field(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    private Long version;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    public boolean isAtivo() {
        return this.status == Status.ATIVO;
    }

    public String getContatoPrincipal() {
        return this.whatsapp != null ? this.whatsapp : this.telefone;
    }
}