package motta.dev.MyBimed.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.enums.Chat;
import motta.dev.MyBimed.enums.StatusChat;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.ChatRepository;
import motta.dev.MyBimed.repository.MensagemRepository;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MensagemService {

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;

    // Enviar uma mensagem de texto, midia ou outro formato

    public MensagemModel enviarMensagem(UUID chatId, UUID remetenteId, String conteudo, TipoMensagem tipo, byte[] arquivo, String nomeArquivo) throws IOException {
        var chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        var remetente = userRepository.findById(remetenteId)
                .orElseThrow(() -> new ResourceNotFoundException("Remetente não encontrado"));

        String urlArquivo = null;

        if (tipo != TipoMensagem.TEXTO && arquivo != null) {
            urlArquivo = fileService.uploadFileFromBytes(arquivo, "MyBimed/uploads/" + nomeArquivo);
        }

        var mensagem = MensagemModel.builder()
                .chat(chat)
                .remetente(remetente)
                .conteudo(conteudo)
                .tipoMensagem(tipo)
                .urlArquivo(urlArquivo)
                .statusMensagem(StatusMensagem.ENVIADO)
                .enviadoEm(LocalDateTime.now())
                .build();

        var salva = mensagemRepository.save(mensagem);

        // Emitir via WebSocket
        simpMessagingTemplate.convertAndSend("/topic/chats/" + chat.getId(), salva);

        return salva;
    }

    // Marcar uma mensagem como entregue
    @Transactional
    public MensagemModel marcarComoEntregue(UUID mensagemId) {
        MensagemModel mensagemModel = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));

        mensagemModel.setStatusMensagem(StatusMensagem.ENTREGUE);
        mensagemModel.setEntregueEm(LocalDateTime.now());

        return mensagemRepository.save(mensagemModel);
    }

    // Marcar uma mensagem como lida
    @Transactional
    public MensagemModel marcarComoLida(UUID mensagemId) {
        MensagemModel mensagemModel = mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new ResourceNotFoundException("Mensagem não encontrada"));

        mensagemModel.setStatusMensagem(StatusMensagem.LIDA);
        mensagemModel.setLidoEm(LocalDateTime.now());

        return mensagemRepository.save(mensagemModel);
    }

    // Buscar todas as mensagens de um chat
    public List<MensagemModel> buscarMensagemPorChat(UUID chatId) {
        ChatModel chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        return mensagemRepository.findByChatOrderByEnviadoEmAsc(chat);
    }

    // Buscar as últimas 20 mensagens de um chat (histórico mais recente)
    public List<MensagemModel> buscarUltimasMensagem(UUID chatId) {
        ChatModel chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado"));

        return mensagemRepository.findTop20ByChatOrderByEnviadoEmDesc(chat);

    }

    public void deletarMensagem(UUID mensagemId) {
        if (!mensagemRepository.existsById(mensagemId)) {
            throw new ResourceNotFoundException("Mensagem não encontrada");
        }
        mensagemRepository.deleteById(mensagemId);
    }

    public void enviarMensagemParaWebSocket(MensagemModel mensagemModel) {
        simpMessagingTemplate.convertAndSend(
                "/chat/" + mensagemModel.getChat().getId(),
                mensagemModel
        );
    }
}
