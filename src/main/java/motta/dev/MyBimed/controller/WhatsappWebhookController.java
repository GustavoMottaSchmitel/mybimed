package motta.dev.MyBimed.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.dto.WhatsAppWebhookRequest;
import motta.dev.MyBimed.exception.WebhookVerificationException;
import motta.dev.MyBimed.service.WebhookService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Controller responsável por receber e processar os webhooks do WhatsApp Business API.
 */
@Slf4j
@RestController
@RequestMapping("/api/whatsapp/webhook")
@RequiredArgsConstructor
public class WhatsappWebhookController {

    private final WebhookService webhookService;

    @Value("${whatsapp.webhook.verify.token}")
    private String verifyToken;

    // Cache simples para evitar replay attacks
    private final Map<String, Boolean> processedMessages = new ConcurrentHashMap<>();
    private static final long CACHE_TTL_MS = 60000; // 1 minuto

    /**
     * Endpoint de verificação do webhook exigido pelo WhatsApp.
     *
     * @param mode Modo de verificação ("subscribe")
     * @param challenge Código de desafio para verificação
     * @param verifyToken Token de verificação
     * @return ResponseEntity com o challenge se verificado com sucesso
     */
    @GetMapping
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode") String mode,
            @RequestParam(name = "hub.challenge") String challenge,
            @RequestParam(name = "hub.verify_token") String verifyToken) {

        final String logPrefix = "[WHATSAPP-WEBHOOK-VERIFY]";
        log.info("{} Iniciando verificação - Mode: {}", logPrefix, mode);

        try {
            validateVerification(mode, verifyToken);
            log.info("{} Verificação bem-sucedida", logPrefix);
            return ResponseEntity.ok(challenge);
        } catch (WebhookVerificationException e) {
            log.warn("{} Falha na verificação: {}", logPrefix, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Endpoint principal para recebimento de mensagens do WhatsApp.
     *
     * @param payload Payload JSON da mensagem
     * @param request Objeto HttpServletRequest
     * @return ResponseEntity vazio com status apropriado
     */
    @PostMapping
    public ResponseEntity<Void> handleIncomingMessage(
            @RequestBody String payload,
            HttpServletRequest request) {

        final String logPrefix = "[WHATSAPP-WEBHOOK]";
        log.info("{} Nova mensagem recebida", logPrefix);

        try {
            // Verificação básica de duplicação (opcional)
            if (isDuplicateMessage(payload)) {
                log.warn("{} Mensagem duplicada ignorada", logPrefix);
                return ResponseEntity.ok().build();
            }

            // Processamento assíncrono
            webhookService.processarWebhook(payload);

            log.info("{} Mensagem aceita para processamento", logPrefix);
            return ResponseEntity.accepted().build();

        } catch (Exception e) {
            log.error("{} Erro ao processar mensagem: {}", logPrefix, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    private void validateVerification(String mode, String receivedToken)
            throws WebhookVerificationException {
        if (!"subscribe".equals(mode)) {
            throw new WebhookVerificationException("Modo de verificação inválido");
        }

        if (!verifyToken.equals(receivedToken)) {
            throw new WebhookVerificationException("Token de verificação inválido");
        }
    }

    private boolean isDuplicateMessage(String payload) {
        // Implementação simples de detecção de duplicados
        String messageId = extractMessageId(payload); // Você precisaria implementar isso
        if (messageId == null) return false;

        if (processedMessages.containsKey(messageId)) {
            return true;
        }

        processedMessages.put(messageId, true);
        // Limpeza periódica do cache (em produção use um Cache com TTL)
        new Thread(() -> {
            try {
                Thread.sleep(CACHE_TTL_MS);
                processedMessages.remove(messageId);
            } catch (InterruptedException ignored) {}
        }).start();

        return false;
    }

    private String extractMessageId(String payload) {
        // Implemente a extração do ID único da mensagem do payload
        // Exemplo simplificado:
        try {
            // Usando um parser JSON para extrair o ID
            // Na prática, você usaria seu DTO WhatsAppWebhookRequest
            return "temp-id"; // Substitua pela implementação real
        } catch (Exception e) {
            log.warn("Falha ao extrair ID da mensagem", e);
            return null;
        }
    }
}