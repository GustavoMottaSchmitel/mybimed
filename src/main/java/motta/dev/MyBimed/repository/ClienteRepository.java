package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ClienteModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClienteRepository extends MongoRepository<ClienteModel, String> {
    boolean existsByEmail(String email);
}