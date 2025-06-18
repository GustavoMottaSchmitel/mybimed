package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collation = "arquivo")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArquivoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

}
