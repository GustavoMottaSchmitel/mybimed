package motta.dev.MyBimed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.dto.MensagemCreateDTO;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.exception.MensagemNotFoundException;
import motta.dev.MyBimed.exception.ResourceNotFoundException;
import motta.dev.MyBimed.model.ChatModel;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.model.UserModel;
import motta.dev.MyBimed.repository.ChatRepository;
import motta.dev.MyBimed.repository.MensagemRepository;
import motta.dev.MyBimed.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MensagemService {

    private static final String UPLOAD_FOLDER = "MyBimed/uploads";

    private final MensagemRepository mensagemRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileService fileService;

    /**
     * Envia uma nova mensagem para um chat
     */
    @Transactional
    public MensagemModel enviarMensagem(MensagemCreateDTO dto, byte[] arquivo, String nomeArquivo) {
        log.info("Enviando mensagem para o chat {}", dto.chatId());

        ChatModel chat = buscarChat(dto.chatId());
        UserModel remetente = buscarUsuario(dto.remetenteId());

        String urlArquivo = processarArquivo(dto.tipo(), arquivo, nomeArquivo);

        MensagemModel mensagem = construirMensagem(dto, chat, remetente, urlArquivo);
        MensagemModel mensagemSalva = mensagemRepository.save(mensagem);

        notificarParticipantes(chat.getId(), mensagemSalva);

        return mensagemSalva;
    }

    /**
     * Envia mensagem via WebSocket (com URL de arquivo pré-existente)
     */
    @Transactional
    public MensagemModel enviarMensagemViaWebSocket(MensagemCreateDTO dto) {
        log.debug("Enviando mensagem via WebSocket para o chat {}", dto.chatId());

        ChatModel chat = buscarChat(dto.chatId());
        UserModel remetente = buscarUsuario(dto.remetenteId());

        MensagemModel mensagem = construirMensagem(dto, chat, remetente, dto.urlArquivo());
        MensagemModel mensagemSalva = mensagemRepository.save(mensagem);

        notificarParticipantes(chat.getId(), mensagemSalva);

        return mensagemSalva;
    }

    /**
     * Marca uma mensagem como entregue
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public MensagemModel marcarComoEntregue(String mensagemId) {
        log.info("Marcando mensagem {} como entregue", mensagemId);

        MensagemModel mensagem = buscarMensagem(mensagemId);
        mensagem.setStatus(StatusMensagem.ENTREGUE);
        mensagem.setEntregueEm(LocalDateTime.now());

        return mensagemRepository.save(mensagem);
    }

    /**
     * Marca uma mensagem como lida
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public MensagemModel marcarComoLida(String mensagemId) {
        log.info("Marcando mensagem {} como lida", mensagemId);

        MensagemModel mensagem = buscarMensagem(mensagemId);
        mensagem.setStatus(StatusMensagem.LIDO);
        mensagem.setLidoEm(LocalDateTime.now());

        notificarParticipantes(mensagem.getChat().getId(), mensagem);

        return mensagemRepository.save(mensagem);
    }

    /**
     * Lista mensagens de um chat com paginação
     */
    @Transactional(readOnly = true)
    public Page<MensagemModel> listarMensagensPorChat(String chatId, Pageable pageable) {
        log.debug("Listando mensagens do chat {} - página {}", chatId, pageable.getPageNumber());

        if (!chatRepository.existsById(chatId)) {
            throw new ResourceNotFoundException("Chat não encontrado com ID: " + chatId);
        }

        return mensagemRepository.findByChatOrderByEnviadoEmAsc(chatId, pageable);
    }

    /**
     * Remove uma mensagem do sistema
     */
    @Transactional
    public void deletarMensagem(String mensagemId) {
        log.warn("Deletando mensagem {}", mensagemId);

        if (!mensagemRepository.existsById(mensagemId)) {
            throw new MensagemNotFoundException(mensagemId);
        }

        mensagemRepository.deleteById(mensagemId);
    }

    // Métodos auxiliares privados

    private ChatModel buscarChat(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat não encontrado com ID: " + chatId));
    }

    private UserModel buscarUsuario(String usuarioId) {
        return userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
    }

    private MensagemModel buscarMensagem(String mensagemId) {
        return mensagemRepository.findById(mensagemId)
                .orElseThrow(() -> new MensagemNotFoundException(mensagemId));
    }

    private String processarArquivo(TipoMensagem tipo, byte[] arquivo, String nomeArquivo) {
        if (tipo == TipoMensagem.TEXTO || arquivo == null) {
            return null;
        }

        try {
            return fileService.uploadFileFromBytes(arquivo, UPLOAD_FOLDER + "/" + nomeArquivo);
        } catch (Exception e) {
            log.error("Falha ao processar arquivo da mensagem", e);
            return null;
        }
    }

    private MensagemModel construirMensagem(
            MensagemCreateDTO dto,
            ChatModel chat,
            UserModel remetente,
            String urlArquivo) {

        return MensagemModel.builder()
                .chat(chat)
                .remetente(remetente)
                .conteudo(dto.conteudo())
                .tipo(dto.tipo())
                .urlArquivo(urlArquivo)
                .status(StatusMensagem.ENVIADO)
                .enviadoEm(LocalDateTime.now())
                .build();
    }

    private void notificarParticipantes(String chatId, MensagemModel mensagem) {
        try {
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, mensagem);
            log.debug("Notificação enviada para o chat {}", chatId);
        } catch (Exception e) {
            log.error("Falha ao notificar participantes do chat", e);
        }
    }
}