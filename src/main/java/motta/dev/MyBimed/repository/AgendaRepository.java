package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.AgendaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AgendaRepository extends JpaRepository<AgendaModel, UUID> {
}
