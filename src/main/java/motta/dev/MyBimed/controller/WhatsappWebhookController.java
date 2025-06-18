package motta.dev.MyBimed.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.service.WebhookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por receber os eventos do WhatsApp Cloud API.
 */
@RestController
@RequestMapping("/whatsapp/webhook")
@RequiredArgsConstructor
@Slf4j
public class WhatsappWebhookController {

    private final WebhookService whatsappWebhookService;

    // Token que você cadastra na configuração do webhook no Meta Developers
    private static final String VERIFY_TOKEN = "seu-token-aqui";

    /**
     * Endpoint de verificação do webhook pelo Meta/WhatsApp (GET)
     */
    @GetMapping
    public ResponseEntity<String> verificarWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String verifyToken
    ) {
        log.info("Tentativa de verificação do webhook. Mode={}, Token={}", mode, verifyToken);

        if ("subscribe".equals(mode) && VERIFY_TOKEN.equals(verifyToken)) {
            log.info("Webhook verificado com sucesso.");
            return ResponseEntity.ok(challenge);
        } else {
            log.warn("Falha na verificação do webhook. Token inválido.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verificação falhou.");
        }
    }

    /**
     * Endpoint que recebe os eventos do WhatsApp (mensagens, status, etc.)
     */
    @PostMapping
    public ResponseEntity<Void> receberWebhook(
            @RequestBody String payload,
            HttpServletRequest request
    ) {
        log.info("🔔 Webhook recebido do WhatsApp: {}", payload);

        try {
            whatsappWebhookService.processarWebhook(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("❌ Erro ao processar webhook: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}