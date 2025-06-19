package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.TokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<TokenModel, String> {
    List<TokenModel> findAllByUserId(String userId);
    Optional<TokenModel> findByToken(String token);
}