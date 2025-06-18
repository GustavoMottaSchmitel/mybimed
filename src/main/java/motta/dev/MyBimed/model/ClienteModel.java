package motta.dev.MyBimed.model;

import lombok.*;
import motta.dev.MyBimed.enums.Status;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Document(collection = "cliente")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClienteModel {

    @Id
    private UUID id;

    private String nome;

    private String especialidade;

    private String email;

    private String telefone;

    private String whatsapp;

    private Status status;

    @CreationTimestamp
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    private LocalDateTime atualizadoEm;

}