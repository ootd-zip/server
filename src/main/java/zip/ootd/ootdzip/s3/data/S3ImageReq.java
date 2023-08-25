package zip.ootd.ootdzip.s3.data;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class S3ImageReq {

    private List<MultipartFile> ootdImages;
}
