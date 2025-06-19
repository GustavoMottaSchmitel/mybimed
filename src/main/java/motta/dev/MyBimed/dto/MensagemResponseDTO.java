package motta.dev.MyBimed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import motta.dev.MyBimed.enums.StatusMensagem;
import motta.dev.MyBimed.enums.TipoMensagem;
import motta.dev.MyBimed.model.MensagemModel;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record MensagemResponseDTO(
        @Schema(description = "ID da mensagem", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String id,

        @Schema(description = "ID do chat", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String chatId,

        @Schema(description = "ID do remetente", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String remetenteId,

        @Schema(description = "Conteúdo da mensagem", example = "Olá, como posso ajudar?")
        String conteudo,

        @Schema(description = "Tipo da mensagem", example = "TEXTO")
        TipoMensagem tipo,

        @Schema(description = "URL do arquivo (para mensagens com mídia)", example = "https://example.com/file.jpg")
        String urlArquivo,

        @Schema(description = "Status da mensagem", example = "ENVIADO")
        StatusMensagem status,

        @Schema(description = "Data de envio", example = "2023-01-01T12:00:00")
        LocalDateTime enviadoEm,

        @Schema(description = "Data de entrega", example = "2023-01-01T12:00:05")
        LocalDateTime entregueEm,

        @Schema(description = "Data de leitura", example = "2023-01-01T12:01:00")
        LocalDateTime lidoEm
) {
    public MensagemResponseDTO(MensagemModel model) {
        this(
                model.getId(),
                model.getChat().getId(),
                model.getRemetente().getId(),
                model.getConteudo(),
                model.getTipo(),
                model.getUrlArquivo(),
                model.getStatus(),
                model.getEnviadoEm(),
                model.getEntregueEm(),
                model.getLidoEm()
        );
    }
}