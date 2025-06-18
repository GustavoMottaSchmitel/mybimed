package motta.dev.MyBimed.controller;

import lombok.RequiredArgsConstructor;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.service.MensagemService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
    public MensagemModel enviarMensagem(
            @RequestParam UUID chatId,
            @RequestParam UUID remetenteID,
            @RequestParam String conteudo,
            @RequestParam TipoMensagem tipo,
            @RequestParam(required = false) String urlArquivo
            ) {
        MensagemModel mensagem = mensagemService.enviarMensagem(chatId, remetenteID, conteudo
        , tipo, urlArquivo.getBytes());

        // Notificar via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chatId,
                mensagem
        );

        return mensagem;
    }

    // Buscar mensagens de um chat (histórico completo)
    @GetMapping("/{chatId}")
    public List<MensagemModel> buscarMensagens(@PathVariable UUID chatId) {
        return mensagemService.buscarMensagemPorChat(chatId);
    }

    // Buscar últimas 20 mensagens
    @GetMapping("/{chatId}/ultimas")
    public List<MensagemModel> buscarUltimasMensagens(@PathVariable UUID chatID) {
        return mensagemService.buscarUltimasMensagem(chatID);
    }

    // Marcar como entregue
    @PostMapping("/{mensagemId}/entregue")
    public MensagemModel marcarComoEntregue(@PathVariable UUID mensagemId) {
        return mensagemService.marcarComoEntregue(mensagemId);
    }

    // Marcar como lida
    @PostMapping("/{mensagemId}/lida")
    public MensagemModel marcarComoLida(@PathVariable UUID mensagemId) {
        return mensagemService.marcarComoLida(mensagemId);
    }

    // Deletar mensagem
    @DeleteMapping("/{mensagemId}")
    public void deletarMensagem(@PathVariable UUID mensagemId) {
        mensagemService.deletarMensagem(mensagemId);
    }

    @MessageMapping("/chat.enviar")
    @SendTo("/topic/chat")
    public MensagemModel enviarMensagemViaWebSocket(@Payload MensagemModel mensagemPayLoad) {
        MensagemModel mensagem = mensagemService.enviarMensagem(
                mensagemPayLoad.getChat().getId(),
                mensagemPayLoad.getRemetente().getId(),
                mensagemPayLoad.getConteudo(),
                mensagemPayLoad.getTipoMensagem(),
                mensagemPayLoad.getUrlArquivo()
        );

        return mensagem;
    }

}
