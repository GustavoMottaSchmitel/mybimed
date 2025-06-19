package motta.dev.MyBimed.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.Map;

@Slf4j
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud_name:}")
    private String cloudName;

    @Value("${cloudinary.api_key:}")
    private String apiKey;

    @Value("${cloudinary.api_secret:}")
    private String apiSecret;

    @Value("${cloudinary.folder:mybimed}")
    private String uploadFolder;

    @PostConstruct
    public void init() {
        if (!isConfigValid()) {
            log.error("Configuração do Cloudinary inválida. Verifique as propriedades.");
            throw new IllegalStateException("Configuração do Cloudinary inválida");
        }
        log.info("Configuração do Cloudinary validada com sucesso");
    }

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        );

        Cloudinary cloudinary = new Cloudinary(config);
        log.info("Bean Cloudinary configurado para a conta: {}", cloudName);
        return cloudinary;
    }

    @Bean
    public String cloudinaryUploadFolder() {
        return uploadFolder;
    }

    private boolean isConfigValid() {
        return StringUtils.hasText(cloudName) &&
                StringUtils.hasText(apiKey) &&
                StringUtils.hasText(apiSecret);
    }
}