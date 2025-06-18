package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ClienteRepository extends MongoRepository<ClienteModel, UUID> {
    boolean existsByEmail(String email);
}
