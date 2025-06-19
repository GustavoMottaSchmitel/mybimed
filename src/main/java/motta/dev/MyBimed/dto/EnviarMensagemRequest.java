package motta.dev.MyBimed.dto;

import lombok.Data;
import motta.dev.MyBimed.enums.TipoMensagem;

import java.util.UUID;

@Data
public class EnviarMensagemRequest {
    private UUID chatId;
    private UUID remetenteId;
    private String conteudo;
    private TipoMensagem tipo;
    private String urlArquivo; // Opcional, pode ser nulo
}
