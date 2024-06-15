package zip.ootd.ootdzip.images.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.images.data.ImagesReq;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImagesService {

    private final ImagesAsyncService imagesAsyncService;

    @Value("${cloud.aws.s3.url.prefix}")
    private String urlPrefix;

    private static final String FILE_EXTENSION = ".jpg";

    /**
     * 프론트에는 사진 url 을 만들어서 먼저 반환해줍니다.(실제로 이미지가 저장되지 않은상태)
     * 그리고 saveImageToS3 함수를 통해 비동기로 s3 로 이미지를 저장합니다.
     */
    public List<String> getUrls(ImagesReq request) {

        List<MultipartFile> images = request.getImages();

        return images.stream()
                .map(i -> {
                    String fileName = makeFileName();
                    imagesAsyncService.upload(i, fileName);
                    return urlPrefix + fileName + FILE_EXTENSION;
                })
                .collect(Collectors.toList());
    }

    private String makeFileName() {
        return UUID.randomUUID() + "_" + LocalDate.now();
    }
}
