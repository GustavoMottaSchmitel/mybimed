package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<ClienteModel, UUID> {
    boolean existsByEmail(String email);
}
