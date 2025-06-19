package motta.dev.MyBimed.model;

import jakarta.persistence.PrePersist;
import lombok.*;
import motta.dev.MyBimed.enums.StatusReuniao;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "reunioes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReuniaoModel {

    @Id
    private String id;

    @Field(name = "titulo")
    private String titulo;

    @Field(name = "descricao")
    private String descricao;

    @DBRef(lazy = true)
    @Field(name = "agenda")
    private AgendaModel agenda;

    @DBRef(lazy = true)
    @Field(name = "participantes")
    private List<UserModel> participantes;

    @Field(name = "link")
    private String link;

    @Field(name = "data_inicio")
    private LocalDateTime dataInicio;

    @Field(name = "data_fim")
    private LocalDateTime dataFim;

    @Field(name = "status")
    private StatusReuniao status;

    @CreatedDate
    @Field(name = "criado_em")
    private LocalDateTime criadoEm;

    @LastModifiedDate
    @Field(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Version
    private Long version; // Para controle de concorrência

    // Método para gerar ID
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }

    // Método para verificar se a reunião está ativa
    public boolean isAtiva() {
        return status == StatusReuniao.AGENDADA || status == StatusReuniao.EM_ANDAMENTO;
    }
}