package motta.dev.MyBimed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import motta.dev.MyBimed.enums.TipoMensagem;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record MensagemCreateDTO(
        @NotNull @Schema(description = "ID do chat", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String chatId,

        @NotNull @Schema(description = "ID do remetente", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        String remetenteId,

        @Schema(description = "Conteúdo da mensagem", example = "Olá, como posso ajudar?")
        String conteudo,

        @NotNull @Schema(description = "Tipo da mensagem", example = "TEXTO")
        TipoMensagem tipo,

        @Schema(description = "URL do arquivo (para mensagens com mídia)", example = "https://example.com/file.jpg")
        String urlArquivo
) {}