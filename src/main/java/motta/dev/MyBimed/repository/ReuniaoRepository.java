package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.ReuniaoModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReuniaoRepository extends JpaRepository<ReuniaoModel, UUID> {

    List<ReuniaoModel> findByStatus(String status);
}
