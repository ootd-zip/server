package zip.ootd.ootdzip.s3.data;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class S3ImageReq {

    private List<MultipartFile> images;
}
