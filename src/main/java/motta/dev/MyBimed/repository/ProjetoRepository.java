package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ProjetoModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjetoRepository extends JpaRepository<ProjetoModel, UUID> {
    List<ProjetoModel> findByTituloContainingIgnoreCase(String titulo);

    boolean existsByTituloIgnoreCase(String titulo);
}
