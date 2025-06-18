package motta.dev.MyBimed.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "arquivo")
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
