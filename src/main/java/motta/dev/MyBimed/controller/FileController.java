package motta.dev.MyBimed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import motta.dev.MyBimed.dto.FileUploadResponse;
import motta.dev.MyBimed.exception.FileUploadException;
import motta.dev.MyBimed.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/arquivos")
@RequiredArgsConstructor
@Tag(name = "Gerenciamento de Arquivos", description = "API para upload e gerenciamento de arquivos")
public class FileController {

    private final FileService fileService;

    // Tipos MIME permitidos
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_PNG_VALUE,
            MediaType.APPLICATION_PDF_VALUE,
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    // Tamanho máximo do arquivo (5MB)
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "MyBimed/uploads") String folder) {

        try {
            log.info("Recebendo arquivo para upload: {} ({} bytes)",
                    file.getOriginalFilename(), file.getSize());

            validateFile(file);

            String fileUrl = fileService.uploadFile(file, folder);

            // Usando o construtor completo
            FileUploadResponse response = new FileUploadResponse(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getSize(),
                    fileUrl,
                    null
            );

            log.info("Arquivo {} enviado com sucesso para {}", file.getOriginalFilename(), fileUrl);
            return ResponseEntity.ok(response);

        } catch (FileUploadException e) {
            return ResponseEntity.badRequest().body(new FileUploadResponse(e.getMessage()));
        } catch (Exception e) {
            log.error("Erro interno ao processar upload", e);
            return ResponseEntity.internalServerError()
                    .body(new FileUploadResponse("Erro interno no servidor"));
        }
    }

    private void validateFile(MultipartFile file) throws FileUploadException {
        // Verifica se o arquivo está vazio
        if (file.isEmpty()) {
            throw new FileUploadException("O arquivo está vazio");
        }

        // Verifica o tamanho do arquivo
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileUploadException(
                    String.format("Tamanho do arquivo excede o limite de %dMB", MAX_FILE_SIZE / (1024 * 1024))
            );
        }

        // Verifica o tipo de conteúdo
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new FileUploadException(
                    String.format("Tipo de arquivo não permitido. Tipos aceitos: %s", ALLOWED_CONTENT_TYPES)
            );
        }
    }
}