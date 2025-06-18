package motta.dev.MyBimed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Document(collation = "tokens")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenModel {

    @Id
    private UUID id;
    private String token;
    private boolean expired;
    private boolean revoked;
    private UUID userId;
}
