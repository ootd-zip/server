package zip.ootd.ootdzip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Configuration
public interface S3Config {
    public List<String> uploadImageListToS3(List<MultipartFile> imageList);
}
