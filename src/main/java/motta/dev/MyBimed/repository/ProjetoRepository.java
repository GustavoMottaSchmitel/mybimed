package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ProjetoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends MongoRepository<ProjetoModel, String> {
    List<ProjetoModel> findByTituloContainingIgnoreCase(String titulo);
    boolean existsByTituloIgnoreCase(String titulo);
}