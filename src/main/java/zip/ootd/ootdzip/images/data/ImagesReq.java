package zip.ootd.ootdzip.images.data;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class ImagesReq {

    private List<MultipartFile> images;
}
