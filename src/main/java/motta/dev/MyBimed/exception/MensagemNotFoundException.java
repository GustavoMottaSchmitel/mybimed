package motta.dev.MyBimed.exception;

public class MensagemNotFoundException extends RuntimeException {
    public MensagemNotFoundException(String id) {
        super("Mensagem não encontrada com ID: " + id);
    }
}