package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemModel, UUID> {
    List<MensagemModel> findByChatId(UUID chatid);

    void deleteByChatId(UUID chatId);
}
