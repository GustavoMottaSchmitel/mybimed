package motta.dev.MyBimed.repository;

import motta.dev.MyBimed.enums.StatusChat;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends MongoRepository<ChatModel, String> {

    Optional<ChatModel> findByNome(String nome);
    List<ChatModel> findByResponsavelId(String responsavelId);
    List<ChatModel> findByResponsavel(UserModel responsavel);
    List<ChatModel> findByStatus(StatusChat status);
    List<ChatModel> findByParticipantesContaining(UserModel participante);
    List<ChatModel> findByParticipantesId(String participantesId);
    List<ChatModel> findByNomeContainingIgnoreCase(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
