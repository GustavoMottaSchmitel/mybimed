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

    public ChatModel createChat(String nome, UUID responsavelId, List<UUID> participantes) {
        var responsavel = userRepository.findById(responsavelId)
                .orElseThrow(() -> new ResourceNotFoundException("Responsável não encontrado"));
        List<UserModel> participantesList = userRepository.findAllById(participantes);

        var chat = ChatModel.builder()
                .nome(nome)
                .responsavel(responsavel)
                .participantes(participantesList)
                .status(StatusChat.ABERTO)
                .build();

        var chatSalvo = chatRepository.save(chat);

        salvarHistorico(chatSalvo, "Chat criado com responsável " + responsavel.getNome());

        return chatSalvo;
    }

    public ChatModel atualizarStatus(UUID chatId, StatusChat status) {
        var chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        chat.setStatus(status);
        chatRepository.save(chat);

        salvarHistorico(chat, "Status alterado para " + status);

        return chat;
    }

    public ChatModel atualizarResponsavel(UUID chatId, UUID novoResponsavelId) {
        var chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat nã oencontrado"));

        var novoResponsavel = userRepository.findById(novoResponsavelId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario não encontrado"));

        chat.setResponsavel(novoResponsavel);
        chatRepository.save(chat);

        salvarHistorico(chat, "Responsavel alterado para " + novoResponsavel.getNome());

        return chat;
    }

    public ChatModel adicionarParticipante(UUID chatId, UUID participanteId) {
        var chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        var participante = userRepository.findById(participanteId)
                .orElseThrow(() -> new ResourceNotFoundException("Participante não encontrado"));

        if (chat.getParticipantes().contains(participante)) {
            throw new ResourceAlreadyExistsException("Participante já está no chat!");
        }

        chat.getParticipantes().add(participante);
        chatRepository.save(chat);

        salvarHistorico(chat, "Participante " + participante.getNome() + "adicionado");

        return chat;
    }

    public ChatModel removerParticipante(UUID chatId, UUID participanteID) {
        var chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        boolean removed = chat.getParticipantes().removeIf(p -> p.getId().equals(participanteID));

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

    public ChatModel getChatById(UUID id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));
    }

    public ChatModel updateChat(UUID id, ChatModel chatUpdate) {
        ChatModel chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado para o ID informado"));

        if (!chat.getNome().equalsIgnoreCase(chatUpdate.getNome()) && chatRepository.existsByNomeIgnoreCase(chatUpdate.getNome())) {
            throw new ResourceAlreadyExistsException("Ja existe outro chat com este nome");
        }

        chat.setNome(chatUpdate.getNome());
        chat.setChatTipo(chatUpdate.getChatTipo());

        chatRepository.save(chat);

        salvarHistorico(chat, "Chat atualiazdo");

        return chat;
    }

    public void deleteChat(UUID id) {
        ChatModel chat = chatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado para o ID informado"));

        chatRepository.delete(chat);

        salvarHistorico(chat, "Chat deletado");
    }

    // Historico interno
    private void salvarHistorico(ChatModel chat, String descricao) {
        var historico = HistoricoChatModel.builder()
                .chat(chat)
                .realizadoEm(LocalDateTime.now())
                .descricao(descricao)
                .build();
        historicoChatRepository.save(historico);
    }
}
