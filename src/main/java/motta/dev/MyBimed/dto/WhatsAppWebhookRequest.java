package motta.dev.MyBimed.dto;

import lombok.Data;

@Data
public class WhatsAppWebhookRequest {
    private String telefone;
    private String nome;
    private String conteudo;
    private String urlArquivo;
    private String tipoMensagem;
}
