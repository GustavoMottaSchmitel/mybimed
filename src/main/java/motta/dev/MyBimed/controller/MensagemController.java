package motta.dev.MyBimed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.dto.MensagemCreateDTO;
import motta.dev.MyBimed.dto.MensagemResponseDTO;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.exception.MensagemNotFoundException;
import motta.dev.MyBimed.model.MensagemModel;
import motta.dev.MyBimed.service.MensagemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/mensagens")
@RequiredArgsConstructor
@Tag(name = "Mensagens", description = "API para gerenciamento de mensagens de chat")
public class MensagemController {

    private final MensagemService mensagemService;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Enviar mensagem",
            description = "Envia uma nova mensagem para um chat",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Mensagem enviada com sucesso",
                            content = @Content(schema = @Schema(implementation = MensagemResponseDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos"),
                    @ApiResponse(responseCode = "404", description = "Chat ou remetente não encontrado")
            })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MensagemResponseDTO> enviarMensagem(
            @Valid MensagemCreateDTO mensagemDTO,
            @RequestParam(required = false) MultipartFile arquivo) throws IOException {

        log.info("Recebendo mensagem para o chat: {}", mensagemDTO.chatId());

        MensagemModel mensagem = mensagemService.enviarMensagem(
                mensagemDTO,
                arquivo != null ? arquivo.getBytes() : null,
                arquivo != null ? arquivo.getOriginalFilename() : null
        );

        // Notificar via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/chat/" + mensagemDTO.chatId(),
                new MensagemResponseDTO(mensagem)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(new MensagemResponseDTO(mensagem));
    }

    @Operation(summary = "Listar mensagens",
            description = "Recupera mensagens de um chat com paginação",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Mensagens recuperadas com sucesso",
                            content = @Content(schema = @Schema(implementation = Page.class)))
            })
    @GetMapping("/{chatId}")
    public ResponseEntity<Page<MensagemResponseDTO>> listarMensagens(
            @Parameter(description = "ID do chat") @PathVariable String chatId,
            @PageableDefault(size = 20) Pageable pageable) {

        log.debug("Buscando mensagens para o chat {} - página {}", chatId, pageable.getPageNumber());
        return ResponseEntity.ok(mensagemService.listarMensagensPorChat(chatId, pageable));
    }

    @Operation(summary = "Marcar como entregue",
            description = "Atualiza o status de uma mensagem para 'entregue'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status atualizado",
                            content = @Content(schema = @Schema(implementation = MensagemResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Mensagem não encontrada")
            })
    @PatchMapping("/{mensagemId}/entregue")
    public ResponseEntity<MensagemResponseDTO> marcarComoEntregue(
            @Parameter(description = "ID da mensagem") @PathVariable String mensagemId) {

        log.info("Marcando mensagem {} como entregue", mensagemId);
        return ResponseEntity.ok(new MensagemResponseDTO(
                mensagemService.marcarComoEntregue(mensagemId)
        ));
    }

    @Operation(summary = "Marcar como lida",
            description = "Atualiza o status de uma mensagem para 'lida'",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Status atualizado",
                            content = @Content(schema = @Schema(implementation = MensagemResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Mensagem não encontrada")
            })
    @PatchMapping("/{mensagemId}/lida")
    public ResponseEntity<MensagemResponseDTO> marcarComoLida(
            @Parameter(description = "ID da mensagem") @PathVariable UUID mensagemId) {

        log.info("Marcando mensagem {} como lida", mensagemId);
        return ResponseEntity.ok(new MensagemResponseDTO(
                mensagemService.marcarComoLida(mensagemId)
        ));
    }

    @Operation(summary = "Deletar mensagem",
            description = "Remove uma mensagem do sistema",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Mensagem removida"),
                    @ApiResponse(responseCode = "404", description = "Mensagem não encontrada")
            })
    @DeleteMapping("/{mensagemId}")
    public ResponseEntity<Void> deletarMensagem(
            @Parameter(description = "ID da mensagem") @PathVariable UUID mensagemId) {

        log.warn("Deletando mensagem {}", mensagemId);
        mensagemService.deletarMensagem(mensagemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Enviar via WebSocket",
            description = "Endpoint WebSocket para envio de mensagens em tempo real",
            hidden = true)
    @MessageMapping("/chat.enviar")
    @SendTo("/topic/chat")
    public MensagemResponseDTO enviarMensagemViaWebSocket(@Payload MensagemCreateDTO mensagemDTO) {
        log.debug("Recebendo mensagem via WebSocket para o chat: {}", mensagemDTO.chatId());
        return new MensagemResponseDTO(
                mensagemService.enviarMensagemViaWebSocket(mensagemDTO)
        );
    }

    @ExceptionHandler(MensagemNotFoundException.class)
    public ResponseEntity<String> handleMensagemNotFound(MensagemNotFoundException ex) {
        log.error("Mensagem não encontrada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}