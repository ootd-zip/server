package zip.ootd.ootdzip.config;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Config {
    public List<String> uploadImageListToS3(List<MultipartFile> imageList);
}
