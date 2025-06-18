package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ClienteModel;
import motta.dev.MyBimed.model.HistoricoChatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface HistoricoChatRepository extends MongoRepository<HistoricoChatModel, UUID> {
}
