package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.AgendaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgendaRepository extends MongoRepository<AgendaModel, String   > {
}
