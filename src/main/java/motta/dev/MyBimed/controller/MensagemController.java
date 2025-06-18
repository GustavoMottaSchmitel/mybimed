package motta.dev.MyBimed.controller;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.service.MensagemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/mensagens")
@RestController
public class MensagemController {

    private final MensagemService mensagemService;
    private final SimpMessagingTemplate messagingTemplate;

    // Enviar mensagem (REST)
    @PostMapping("/enviar")
    public ResponseEntity<MensagemModel> enviarMensagem(
            @RequestParam UUID chatId,
            @RequestParam UUID remetenteID,
            @RequestParam String conteudo,
            @RequestParam TipoMensagem tipo,
            @RequestParam(required = false) MultipartFile urlArquivo
    ) throws IOException {

        String nomeArquivo = (urlArquivo != null) ? urlArquivo.getOriginalFilename() : null;
        byte[] arquivoBytes = (urlArquivo != null) ? urlArquivo.getBytes() : null;

        MensagemModel mensagem = mensagemService.enviarMensagem(
                chatId, remetenteID, conteudo, tipo, arquivoBytes, nomeArquivo
        );

        // Notificar via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                mensagem
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(mensagem);
    }

    // Buscar mensagens de um chat (histórico completo)
    @GetMapping("/{chatId}")
    public ResponseEntity<List<MensagemModel>> buscarMensagens(@PathVariable UUID chatId) {
        List<MensagemModel> mensagens = mensagemService.buscarMensagemPorChat(chatId);
        return ResponseEntity.ok(mensagens);
    }

    // Buscar últimas 20 mensagens
    @GetMapping("/{chatId}/ultimas")
    public ResponseEntity<List<MensagemModel>> buscarUltimasMensagens(@PathVariable UUID chatId) {
        List<MensagemModel> mensagens = mensagemService.buscarUltimasMensagem(chatId);
        return ResponseEntity.ok(mensagens);
    }

    // Marcar como entregue
    @PostMapping("/{mensagemId}/entregue")
    public ResponseEntity<MensagemModel> marcarComoEntregue(@PathVariable UUID mensagemId) {
        MensagemModel mensagem = mensagemService.marcarComoEntregue(mensagemId);
        return ResponseEntity.ok(mensagem);
    }

    // Marcar como lida
    @PostMapping("/{mensagemId}/lida")
    public ResponseEntity<MensagemModel> marcarComoLida(@PathVariable UUID mensagemId) {
        MensagemModel mensagem = mensagemService.marcarComoLida(mensagemId);
        return ResponseEntity.ok(mensagem);
    }

    // Deletar mensagem
    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Void> deletarMensagem(@PathVariable UUID mensagemId) {
        mensagemService.deletarMensagem(mensagemId);
        return ResponseEntity.noContent().build();
    }

    @MessageMapping("/chat.enviar")
    @SendTo("/topic/chat")
    public MensagemModel enviarMensagemViaWebSocket(@Payload MensagemModel mensagemPayLoad) {
        return mensagemService.enviarMensagemViaUrl(
                mensagemPayLoad.getChat().getId(),
                mensagemPayLoad.getRemetente().getId(),
                mensagemPayLoad.getConteudo(),
                mensagemPayLoad.getTipoMensagem(),
                mensagemPayLoad.getUrlArquivo()
        );
    }
}