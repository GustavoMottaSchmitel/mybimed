package motta.dev.MyBimed.model;

import lombok.*;
import motta.dev.MyBimed.enums.StatusAgenda;
import motta.dev.MyBimed.enums.TipoAgenda;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "agenda")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgendaModel {

    @Id
    private UUID id;

    private String titulo;

    private TipoAgenda tipoAgenda;

    private LocalDateTime dataInicio;

    private LocalDateTime dataFim;

    private String descricao;

    private List<UserModel> participantes;

    private ClienteModel cliente;

    private StatusAgenda status;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}