package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ReuniaoModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReuniaoRepository extends MongoRepository<ReuniaoModel, String> {
    List<ReuniaoModel> findByStatus(String status);
}