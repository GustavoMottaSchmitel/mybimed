package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ClienteModel;
import motta.dev.MyBimed.model.HistoricoChatModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HistoricoChatRepository extends JpaRepository<HistoricoChatModel, UUID> {
}
