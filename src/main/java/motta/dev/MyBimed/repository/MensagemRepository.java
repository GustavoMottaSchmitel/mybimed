package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensagemRepository extends MongoRepository<MensagemModel, String> {

    List<MensagemModel> findByChatOrderByEnviadoEmAsc(ChatModel chat);
    List<MensagemModel> findByChatAndRemetente(ChatModel chat, UserModel remetente);
    List<MensagemModel> findByChatAndStatus(ChatModel chat, StatusMensagem status);
    List<MensagemModel> findTop20ByChatOrderByEnviadoEmDesc(ChatModel chat);
    Page<MensagemModel> findByChatIdOrderByEnviadoEmDesc(String chatId, Pageable pageable);
    List<MensagemModel> findByChatId(String chatId);
    void deleteByChatId(String chatId);
}