package motta.dev.MyBimed.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import motta.dev.MyBimed.request.WhatsAppWebhookRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final ChatRepository chatRepository;
    private final MensagemRepository mensagemRepository;
    private final MensagemService mensagemService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileService fileService;
    private final ObjectMapper objectMapper;

    public void processarWebhook(String payload) {
        try {
            WhatsAppWebhookRequest webhookRequest = objectMapper.readValue(payload, WhatsAppWebhookRequest.class);

            String telefone = webhookRequest.getTelefone();
            String nome = webhookRequest.getNome();
            String conteudo = webhookRequest.getConteudo();
            String tipoMensagem = webhookRequest.getTipoMensagem();
            String urlMidia = webhookRequest.getUrlArquivo();

            log.info("Processando mensagem de WhatsApp do número: {}", telefone);

            // Verificar se existe usuário para este telefone
            UserModel remetente = userRepository.findByTelefone(telefone)
                    .orElseGet(() -> criarUsuarioWhatsApp(telefone, nome));

            // Verificar se existe chat com este telefone
            ChatModel chat = chatRepository.findByNome(telefone)
                    .orElseGet(() -> criarChatParaWhatsApp(remetente));

            TipoMensagem tipo = obterTipoMensagem(tipoMensagem);

            String urlArquivo = null;
            if (urlMidia != null && !urlMidia.isBlank()) {
                urlArquivo = uploadMidiaParaCloud(urlMidia);
            }

            MensagemModel mensagem = MensagemModel.builder()
                    .chat(chat)
                    .remetente(remetente)
                    .conteudo(conteudo)
                    .tipoMensagem(tipo)
                    .urlArquivo(urlArquivo)
                    .statusMensagem(StatusMensagem.RECEBIDO)
                    .enviadoEm(LocalDateTime.now())
                    .build();

            // Salvar a mensagem no banco de dados
            mensagemRepository.save(mensagem);

            // Emitir a mensagem via WebSocket
            simpMessagingTemplate.convertAndSend("/topic/chats/" + chat.getId(), mensagem);

            log.info("Mensagem processada e enviada para o WebSocket do chat: {}", chat.getId());

        } catch (IOException e) {
            log.error("Erro ao processar o payload do webhook: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao processar o webhook", e);
        }
    }

    private TipoMensagem obterTipoMensagem(String tipoMensagem) {
        try {
            return tipoMensagem != null ? TipoMensagem.valueOf(tipoMensagem.toUpperCase()) : TipoMensagem.TEXTO;
        } catch (IllegalArgumentException e) {
            log.warn("Tipo de mensagem inválido, utilizando o padrão TEXTO.");
            return TipoMensagem.TEXTO;  // Padrão para tipo de mensagem TEXTO
        }
    }

    /**
     * Cria chat automaticamente quando vem uma nova mensagem de um número não cadastrado.
     */
    private ChatModel criarChatParaWhatsApp(UserModel usuario) {
        ChatModel chat = ChatModel.builder()
                .nome(usuario.getTelefone()) // Chat nomeado pelo telefone
                .responsavel(buscarUsuarioSistema()) // Você pode configurar um usuário responsável padrão
                .participantes(List.of(buscarUsuarioSistema()))
                .chatTipo(Chat.WHATSAPP)
                .status(StatusChat.ABERTO)
                .build();
        return chatRepository.save(chat);
    }

    /**
     * Cria usuário automaticamente quando vem uma nova mensagem de um telefone não cadastrado.
     */
    private UserModel criarUsuarioWhatsApp(String telefone, String nome) {
        UserModel user = UserModel.builder()
                .nome(nome != null ? nome : telefone)
                .telefone(telefone)
                .build();
        return userRepository.save(user);
    }

    /**
     * Faz download da mídia via URL (API do WhatsApp) e envia para um serviço de arquivos na nuvem.
     */
    private String uploadMidiaParaCloud(String urlMidia) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlMidia))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] bytes = response.body();
                return fileService.uploadFileFromBytes(bytes, "MyBimed/uploads");
            } else {
                log.error("Falha ao baixar mídia. Código HTTP: {}", response.statusCode());
                throw new RuntimeException("Falha ao baixar mídia. Código HTTP: " + response.statusCode());
            }

        } catch (IOException | InterruptedException e) {
            log.error("Erro ao baixar/upload da mídia do WhatsApp: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao fazer upload da mídia", e);
        }
    }

    /**
     * Usuário responsável padrão.
     */
    private UserModel buscarUsuarioSistema() {
        // Aqui poderia buscar um usuário padrão cadastrado no sistema
        return userRepository.findByEmail("mottaschmitelg@gmail.com")
                .orElseThrow(() -> new RuntimeException("Usuário padrão não encontrado"));
    }
}