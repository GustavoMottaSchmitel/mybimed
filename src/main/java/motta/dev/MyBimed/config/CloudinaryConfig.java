package motta.dev.MyBimed.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "Mybimed",
                "api_key", "979966972565555",
                "api_secret", "4IGtTlmueLtPGGIb3w5TI9rSnXo",
                "secure", true
        ));
    }
}
