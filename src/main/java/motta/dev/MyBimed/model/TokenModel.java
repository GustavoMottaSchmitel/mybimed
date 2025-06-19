package motta.dev.MyBimed.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tokens")
public class TokenModel {

    @Id
    private String id;

    @Field(name = "token")
    private String token;

    @Field(name = "expired")
    private boolean expired;

    @Field(name = "revoked")
    private boolean revoked;

    @Field(name = "user_id")
    private String userId;

    @Field(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field(name = "expires_at")
    private LocalDateTime expiresAt;

    // Método para verificar se o token é válido
    public boolean isValid() {
        return !expired && !revoked && expiresAt.isAfter(LocalDateTime.now());
    }

    // Método para gerar ID automaticamente
    public void generateId() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}