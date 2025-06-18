package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.repository.ChatRepository;
import motta.dev.MyBimed.repository.MensagemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;

    public MensagemModel createMensagem(UUID chatId, MensagemModel mensagem) {
        ChatModel chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        mensagem.setChat(chat);
        return mensagemRepository.save(mensagem);

    }

    public List<MensagemModel> getMensagensByChat(UUID chatId) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        return mensagemRepository.findByChatId(chatId);
    }

    public void deleteMensagem(UUID mensagemId) {
        var mensagem = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));

        mensagemRepository.delete(mensagem);
    }

    public void deleteMensagensByChat(UUID chatId) {
        chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        mensagemRepository.deleteByChatId(chatId);
    }
}
