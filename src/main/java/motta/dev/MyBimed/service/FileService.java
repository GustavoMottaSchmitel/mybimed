package motta.dev.MyBimed.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    /**
     * Faz o upload de um arquivo para o Cloudinary.
     *
     * @param file Arquivo a ser enviado.
     * @param pasta Nome da pasta onde o arquivo será armazenado.
     * @return URL segura do arquivo.
     * @throws IOException Se ocorrer um erro ao fazer o upload do arquivo.
     */
    public String uploadFile(MultipartFile file, String pasta) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", pasta));

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Erro ao fazer upload do arquivo para o Cloudinary", e);
        }
    }

    /**
     * Faz o upload de um arquivo utilizando bytes diretamente.
     *
     * @param bytes Conteúdo do arquivo em formato de byte array.
     * @param pasta Nome da pasta onde o arquivo será armazenado.
     * @return URL segura do arquivo.
     * @throws IOException Se ocorrer um erro ao fazer o upload do arquivo.
     */
    public String uploadFileFromBytes(byte[] bytes, String pasta) throws IOException {
        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(bytes,
                    ObjectUtils.asMap("folder", pasta));

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new IOException("Erro ao fazer upload do arquivo para o Cloudinary", e);
        }
    }
}