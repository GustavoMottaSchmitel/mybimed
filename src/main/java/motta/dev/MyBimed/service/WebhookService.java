package motta.dev.MyBimed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.dto.WhatsAppWebhookRequest;
import motta.dev.MyBimed.enums.*;
import motta.dev.MyBimed.exception.WebhookProcessingException;
import motta.dev.MyBimed.model.*;
import motta.dev.MyBimed.repository.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private static final String DEFAULT_SYSTEM_USER_EMAIL = "suporte@mybimed.com";
    private static final String CLOUD_UPLOAD_PATH = "MyBimed/uploads";
    private static final long MEDIA_PROCESSING_TIMEOUT = 30;

    private final ChatRepository chatRepository;
    private final MensagemRepository mensagemRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FileService fileService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Executor taskExecutor; // Executor assíncrono configurado

    @Async
    @Transactional
    public CompletableFuture<Void> processarWebhook(String payload) {
        return parsePayloadAsync(payload)
                .thenCompose(this::processarMensagemAsync)
                .exceptionally(ex -> {
                    log.error("Falha no processamento do webhook", ex);
                    return null;
                });
    }

    private CompletableFuture<WhatsAppWebhookRequest> parsePayloadAsync(String payload) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return objectMapper.readValue(payload, WhatsAppWebhookRequest.class);
            } catch (Exception e) {
                throw new WebhookProcessingException("Falha ao analisar o payload", e);
            }
        }, taskExecutor);
    }

    private CompletableFuture<Void> processarMensagemAsync(WhatsAppWebhookRequest request) {
        return obterOuCriarUsuarioAsync(request)
                .thenCompose(remetente -> obterOuCriarChatAsync(remetente)
                        .thenCompose(chat -> processarMidiaEEnviarMensagem(request, chat))
                        .orTimeout(MEDIA_PROCESSING_TIMEOUT, TimeUnit.SECONDS));
    }

    private CompletableFuture<UserModel> obterOuCriarUsuarioAsync(WhatsAppWebhookRequest request) {
        return CompletableFuture.supplyAsync(() ->
                        userRepository.findByTelefone(request.getTelefone())
                                .orElseGet(() -> criarUsuarioWhatsApp(request))
                , taskExecutor);
    }

    private CompletableFuture<ChatModel> obterOuCriarChatAsync(UserModel usuario) {
        return CompletableFuture.supplyAsync(() ->
                        chatRepository.findByNome(usuario.getTelefone())
                                .orElseGet(() -> criarChatParaWhatsApp(usuario))
                , taskExecutor);
    }

    private CompletableFuture<Void> processarMidiaEEnviarMensagem(
            WhatsAppWebhookRequest request,
            ChatModel chat) {

        CompletableFuture<String> midiaFuture = Optional.ofNullable(request.getUrlArquivo())
                .filter(url -> !url.isBlank())
                .map(this::processarMidiaAsync)
                .orElse(CompletableFuture.completedFuture(null));

        return midiaFuture.thenCompose(urlArquivo -> {
            MensagemModel mensagem = criarMensagem(request, chat.getResponsavel(), chat, urlArquivo);
            return salvarEEnviarMensagemAsync(mensagem, chat.getId());
        });
    }

    private CompletableFuture<String> processarMidiaAsync(String urlMidia) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                byte[] fileBytes = downloadMidia(urlMidia);
                return fileService.uploadFileFromBytes(fileBytes, CLOUD_UPLOAD_PATH);
            } catch (Exception e) {
                log.error("Falha no processamento de mídia", e);
                return null;
            }
        }, taskExecutor);
    }

    private CompletableFuture<Void> salvarEEnviarMensagemAsync(MensagemModel mensagem, String chatId) {
        return CompletableFuture.runAsync(() -> {
            mensagemRepository.save(mensagem);
            enviarMensagemWebSocket(chatId, mensagem);
        }, taskExecutor);
    }

    private byte[] downloadMidia(String urlMidia) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlMidia))
                .GET()
                .build();

        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new WebhookProcessingException(
                    "Falha ao baixar mídia. Código HTTP: " + response.statusCode());
        }

        return response.body();
    }

    private void enviarMensagemWebSocket(String chatId, MensagemModel mensagem) {
        try {
            messagingTemplate.convertAndSend("/topic/chats/" + chatId, mensagem);
            log.info("Mensagem enviada via WebSocket para o chat: {}", chatId);
        } catch (Exception e) {
            log.error("Falha ao enviar mensagem via WebSocket", e);
        }
    }

    private UserModel criarUsuarioWhatsApp(WhatsAppWebhookRequest request) {
        UserModel user = UserModel.builder()
                .nome(request.getNome() != null ? request.getNome() : request.getTelefone())
                .telefone(request.getTelefone())
                .build();
        return userRepository.save(user);
    }

    private ChatModel criarChatParaWhatsApp(UserModel usuario) {
        UserModel responsavel = userRepository.findByEmail(DEFAULT_SYSTEM_USER_EMAIL)
                .orElseThrow(() -> new WebhookProcessingException("Usuário padrão não encontrado"));

        ChatModel chat = ChatModel.builder()
                .nome(usuario.getTelefone())
                .responsavel(responsavel)
                .participantes(List.of(responsavel))
                .tipo(Chat.WHATSAPP)
                .status(StatusChat.ABERTO)
                .build();

        return chatRepository.save(chat);
    }

    private MensagemModel criarMensagem(
            WhatsAppWebhookRequest request,
            UserModel remetente,
            ChatModel chat,
            String urlArquivo) {

        return MensagemModel.builder()
                .chat(chat)
                .remetente(remetente)
                .conteudo(request.getConteudo())
                .tipo(obterTipoMensagem(request.getTipoMensagem()))
                .urlArquivo(urlArquivo)
                .status(StatusMensagem.RECEBIDO)
                .enviadoEm(LocalDateTime.now())
                .build();
    }

    private TipoMensagem obterTipoMensagem(String tipoMensagem) {
        try {
            return tipoMensagem != null ?
                    TipoMensagem.valueOf(tipoMensagem.toUpperCase()) :
                    TipoMensagem.TEXTO;
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de mensagem inválido: {}, usando padrão TEXTO", tipoMensagem);
            return TipoMensagem.TEXTO;
        }
    }
}