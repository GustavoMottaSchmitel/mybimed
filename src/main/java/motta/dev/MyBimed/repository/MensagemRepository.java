package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MensagemRepository extends JpaRepository<MensagemModel, UUID> {


    // Buscar mensagens de um chat, ordenados por data de envio (ascendente)

    List<MensagemModel> findByChatOrderByEnviadoEmAsc(ChatModel chat);

    // Buscar mensagens de um chat enviados por um usuario especifico

    List<MensagemModel> findByChatAndRemetente(ChatModel chat, UserModel remetente);

    // Buscar não lidos de um chat

    List<MensagemModel> findByChatAndStatusMensagem(ChatModel chat, StatusMensagem statusMensagem);

    // Buscar as últimas N mensagens de um chat

    List<MensagemModel> findTop20ByChatOrderByEnviadoEmDesc(ChatModel chat);

    List<MensagemModel> findByChatId(UUID chatid);

    void deleteByChatId(UUID chatId);
}
