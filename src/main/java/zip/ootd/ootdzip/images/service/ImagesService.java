package zip.ootd.ootdzip.images.service;

import static zip.ootd.ootdzip.images.domain.Images.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.images.domain.Images;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImagesService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.url.prefix}")
    private String urlPrefix;

    private final String tempImageFolder = System.getProperty("user.dir") + "/" + "temp";

    public String makeFileName() {
        return UUID.randomUUID() + "_" + LocalDate.now();
    }

    public String makeImageUrl(String name) {
        return urlPrefix + name + FILE_EXTENSION;
    }

    @Async
    public void upload(MultipartFile multipartFile, String name) {

        File localFile = convertToFile(multipartFile, name);
        checkFile(localFile);

        // 썸네일 업로드
        uploadThumbnail(localFile, name, LARGE);
        uploadThumbnail(localFile, name, MEDIUM);
        uploadThumbnail(localFile, name, SMALL);

        // 원본 업로드, 원본 파일 삭제를 하기때문에 썸네일 파일을 업로드하고 실행해야함
        uploadToS3(localFile, name + FILE_EXTENSION);
    }

    private void uploadThumbnail(File localFile, String name, Integer size) {
        String resizedName = makeResizedFileName(name, size, size);
        File resizedImage = resizeImage(localFile, resizedName, size, size);
        uploadToS3(resizedImage, resizedName + FILE_EXTENSION);
    }

    private void uploadToS3(File localFile, String fileName) {
        try {
            // 업로드
            putS3(localFile, fileName);
        } catch (Exception e) {
            // 이미지 업로드 실패시 파일 추후에 재업로드 하기위해 로컬에 파일을 남겨둠
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }

        deleteFile(localFile);
    }

    // MultipartFile 을 로컬 파일(File)로 변환
    private File convertToFile(MultipartFile multipartFile, String name) {
        try {
            File tempFile = File.createTempFile(name, FILE_EXTENSION, new File(tempImageFolder));
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    // 해당 파일이 이미지 파일인지 체크
    private void checkFile(File file) {
        String type;
        try {
            type = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        if (!type.startsWith("image")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }
    }

    // s3 업로드, fileName 은 이미지 확장까지 붙은 완벽한 이름이어야함 ex) image.jpg
    private String putS3(File file, String fileName) {
        try {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    /**
     * 이미지를 리사이즈 해주는 메서드
     * 해당 메서드는 로컬에 파일을 만드므로 업로드 후 꼭 delete 를 해주어야 합니다.
     */
    private File resizeImage(File file, String name, int width, int height) {
        try {
            File resizedFile = new File(file.getParent(), name + FILE_EXTENSION);
            Thumbnails.of(file)
                    .size(width, height)
                    .outputFormat("jpg")
                    .toFile(resizedFile);
            return resizedFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            throw new CustomException(ErrorCode.IMAGE_DELETE_FAIL);
        }
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
