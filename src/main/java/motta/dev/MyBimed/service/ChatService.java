package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.enums.StatusChat;
import motta.dev.MyBimed.exception.ResourceAlreadyExistsException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.HistoricoChatModel;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.ChatRepository;
import motta.dev.MyBimed.repository.HistoricoChatRepository;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final HistoricoChatRepository historicoChatRepository;

    public ChatModel createChat(String nome, String responsavelId, List<String> participantes) {
        UserModel responsavel = findUserById(responsavelId);
        List<UserModel> participantesList = userRepository.findAllById(participantes);

        ChatModel chat = ChatModel.builder()
                .nome(nome)
                .responsavel(responsavel)
                .participantes(participantesList)
                .status(StatusChat.ABERTO)
                .build();

        ChatModel chatSalvo = chatRepository.save(chat);
        salvarHistorico(chatSalvo, "Chat criado com responsável " + responsavel.getNome());

        return chatSalvo;
    }

    public ChatModel atualizarStatus(String chatId, StatusChat status) {
        ChatModel chat = findChatById(chatId);

        chat.setStatus(status);
        chatRepository.save(chat);
        salvarHistorico(chat, "Status alterado para " + status);

        return chat;
    }

    public ChatModel atualizarResponsavel(String chatId, String novoResponsavelId) {
        ChatModel chat = findChatById(chatId);
        UserModel novoResponsavel = findUserById(novoResponsavelId);

        chat.setResponsavel(novoResponsavel);
        chatRepository.save(chat);
        salvarHistorico(chat, "Responsável alterado para " + novoResponsavel.getNome());

        return chat;
    }

    public ChatModel adicionarParticipante(String chatId, String participanteId) {
        ChatModel chat = findChatById(chatId);
        UserModel participante = findUserById(participanteId);

        if (chat.getParticipantes().contains(participante)) {
            throw new ResourceAlreadyExistsException("Participante já está no chat!");
        }

        chat.getParticipantes().add(participante);
        chatRepository.save(chat);
        salvarHistorico(chat, "Participante " + participante.getNome() + " adicionado");

        return chat;
    }

    public ChatModel removerParticipante(String chatId, String participanteId) {
        ChatModel chat = findChatById(chatId);

        boolean removed = chat.getParticipantes().removeIf(p -> p.getId().equals(participanteId));

        if (!removed) {
            throw new ResourceNotFoundException("Participante não encontrado no chat");
        }

        chatRepository.save(chat);
        salvarHistorico(chat, "Participante removido do chat");

        return chat;
    }

    public List<ChatModel> getAllChats() {
        return chatRepository.findAll();
    }

    public ChatModel getChatById(String id) {
        return findChatById(id);
    }

    public ChatModel updateChat(String id, ChatModel chatUpdate) {
        ChatModel chat = findChatById(id);

        if (!chat.getNome().equalsIgnoreCase(chatUpdate.getNome()) && chatRepository.existsByNomeIgnoreCase(chatUpdate.getNome())) {
            throw new ResourceAlreadyExistsException("Já existe outro chat com este nome");
        }

        chat.setNome(chatUpdate.getNome());
        chat.setTipo(chatUpdate.getTipo());

        chatRepository.save(chat);
        salvarHistorico(chat, "Chat atualizado");

        return chat;
    }

    public void deleteChat(String id) {
        ChatModel chat = findChatById(id);
        chatRepository.delete(chat);
        salvarHistorico(chat, "Chat deletado");
    }

    // Histórico interno
    private void salvarHistorico(ChatModel chat, String descricao) {
        HistoricoChatModel historico = HistoricoChatModel.builder()
                .chat(chat)
                .realizadoEm(LocalDateTime.now())
                .descricao(descricao)
                .build();
        historicoChatRepository.save(historico);
    }

    // Método auxiliar para buscar o chat
    private ChatModel findChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));
    }

    // Método auxiliar para buscar o usuário
    private UserModel findUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
    }
}