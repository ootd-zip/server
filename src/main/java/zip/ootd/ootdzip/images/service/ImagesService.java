package zip.ootd.ootdzip.images.service;

import static zip.ootd.ootdzip.images.domain.Images.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

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
    public void upload(File localFile, String name) {

        // 원본 업로드
        uploadToS3(localFile, name + FILE_EXTENSION);

        // 썸네일 업로드
        uploadThumbnail(localFile, name, LARGE);
        uploadThumbnail(localFile, name, MEDIUM);
        uploadThumbnail(localFile, name, SMALL);

        // 원본과, 썸네일 업로드시에 예외가 발생하면 아래코드가 실행되지 않아 물리파일을 삭제하지 않고 스케줄러가 다시 업로드함.
        // 물리 파일 삭제
        deleteFile(localFile);
    }

    private void uploadThumbnail(File localFile, String name, Integer size) {
        String resizedName = makeResizedFileName(name, size, size);
        File resizedImage = resizeImage(localFile, resizedName, size, size);
        uploadToS3(resizedImage, resizedName + FILE_EXTENSION);
    }

    // fileName 은 이미지 확장까지 붙은 완벽한 이름이어야함 ex) image.jpg
    public void uploadToS3(File localFile, String fileName) {
        try {
            // 업로드
            putS3(localFile, fileName);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_UPLOAD_FAIL);
        }
    }

    // MultipartFile 을 로컬 파일(File)로 변환
    public File convertToFile(MultipartFile multipartFile, String name) {
        try {
            File tempFile = File.createTempFile(name, FILE_EXTENSION, new File(tempImageFolder));
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    // 해당 파일이 이미지 파일인지 체크
    // 너무 고해상도 인지 체크
    public void checkFile(File file) {
        String type;
        try {
            type = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        if (!type.startsWith("image")) {
            throw new CustomException(ErrorCode.INVALID_IMAGE_URL);
        }

        // 사전에 고해상도 이미지 차단
        // 이미지 메타데이터를 통해 해상도를 체크해, 이미지 전체를 메모리에 띄우지 않음
        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                reader.setInput(input, true);

                long width = reader.getWidth(0);
                long height = reader.getHeight(0);

                if (width * height > 64_000_000) { // 64_000_000 이면 썸네일 변환 작업시 64x3 mb 를 사용함
                    // 고해상도 이미지를 올릴시 에러 반환
                    throw new CustomException(ErrorCode.IMAGE_OVER_RESOLUTION);
                }
            }
        } catch (IOException e) {
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
        } catch (OutOfMemoryError error) {
            // checkfile() 에서 고해상도의 경우 한번 걸러주지만, 비동기 작업으로 인한 oom 발생시 처리를 위해
            // 나중에 여유가 되면 처리할 수 있으므로 원본파일 남겨두기위해 별도로 catch
            throw new CustomException(ErrorCode.IMAGE_OVER_RESOLUTION);
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
