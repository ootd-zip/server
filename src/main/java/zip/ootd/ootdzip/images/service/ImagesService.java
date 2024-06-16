package zip.ootd.ootdzip.images.service;

import static zip.ootd.ootdzip.images.domain.Images.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.images.data.ImagesReq;
import zip.ootd.ootdzip.images.domain.Images;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImagesService {

    private final ImagesAsyncService imagesAsyncService;

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.url.prefix}")
    private String urlPrefix;

    /**
     * 프론트에는 사진 url 을 만들어서 먼저 반환해줍니다.(실제로 이미지가 저장되지 않은상태)
     * 그리고 imagesAsyncService 클래스를 통해 비동기로 s3 로 이미지를 저장합니다.
     */
    public List<String> getUrls(ImagesReq request) {

        List<MultipartFile> images = request.getImages();

        return images.stream()
                .map(i -> {
                    String fileName = makeFileName();
                    imagesAsyncService.upload(i, fileName);
                    return makeImageUrl(fileName);
                })
                .collect(Collectors.toList());
    }

    private String makeFileName() {
        return UUID.randomUUID() + "_" + LocalDate.now();
    }

    private String makeImageUrl(String name) {
        return urlPrefix + name + FILE_EXTENSION;
    }

    public void deleteImagesByUrlToS3(Images images) {
        String fileName = getNameFromImageUrl(images.getImageUrl());
        String fileNameBig = getNameFromImageUrl(images.getImageUrlBig());
        String fileNameMedium = getNameFromImageUrl(images.getImageUrlMedium());
        String fileNameSmall = getNameFromImageUrl(images.getImageUrlSmall());
        try {
            amazonS3Client.deleteObject(bucket, fileName);
            amazonS3Client.deleteObject(bucket, fileNameBig);
            amazonS3Client.deleteObject(bucket, fileNameMedium);
            amazonS3Client.deleteObject(bucket, fileNameSmall);
        } catch (SdkClientException e) {
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAIL);
        }
    }
}
