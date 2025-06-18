package motta.dev.MyBimed.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serial;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String pasta) throws IOException {
        Map upload = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", pasta
                ));

        return upload.get("secure_url").toString();
    }

    public String uploadFileFromBytes(byte[] bytes, String pasta) throws IOException {
        Map upload = cloudinary.uploader().upload(bytes,
                ObjectUtils.asMap("folder", pasta));
        return upload.get("secure_url").toString();
    }

}
