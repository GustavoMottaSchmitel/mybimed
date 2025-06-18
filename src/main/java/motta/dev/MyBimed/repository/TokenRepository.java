package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends MongoRepository<TokenModel, UUID> {
    List<TokenModel> findAllByUserId(UUID userId);
    Optional<TokenModel> findByToken(String token);
}
