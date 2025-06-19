package motta.dev.MyBimed.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta de upload de arquivo")
public class FileUploadResponse {

    @Schema(description = "Nome original do arquivo", example = "documento.pdf")
    private String fileName;

    @Schema(description = "Tipo MIME do arquivo", example = "application/pdf")
    private String fileType;

    @Schema(description = "Tamanho do arquivo em bytes", example = "10240")
    private long size;

    @Schema(description = "URL de acesso ao arquivo",
            example = "https://storage.cloud.com/MyBimed/uploads/documento.pdf")
    private String fileUrl;

    @Schema(description = "Mensagem de erro, se aplicável")
    private String errorMessage;

    // Construtor para erros
    public FileUploadResponse(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}