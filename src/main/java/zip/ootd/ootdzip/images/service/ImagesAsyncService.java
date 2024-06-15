package zip.ootd.ootdzip.images.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;

/**
 * 비동기 서비스를 분리한 이유는
 * 같은 클래스내에 있는 메서드가 비동기메서드를 호출시 self-invocation 이 발생(inner method 사용불가) 하기때문에
 * 따로 분리해두는게 관리하기 편하다고 판단 했습니다.
 */
@RequiredArgsConstructor
@Service
public class ImagesAsyncService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String FILE_EXTENSION = ".jpg";

    @Async
    public void upload(MultipartFile multipartFile, String fileName) {

        File localFile = convertToFile(multipartFile, fileName);
        checkFile(localFile);

        try {
            // 원본 업로드
            putS3(localFile, fileName + FILE_EXTENSION);

            // 썸네일 업로드
            String imageName1 = makeResizedFileName(fileName, 800, 800) + FILE_EXTENSION;
            File resizedImage1 = resizeImage(localFile, imageName1, 800, 800);
            putS3(resizedImage1, imageName1);

            String imageName2 = makeResizedFileName(fileName, 400, 400) + FILE_EXTENSION;
            File resizedImage2 = resizeImage(localFile, imageName2, 400, 400);
            putS3(resizedImage2, imageName2);

            String imageName3 = makeResizedFileName(fileName, 200, 200) + FILE_EXTENSION;
            File resizedImage3 = resizeImage(localFile, imageName3, 200, 200);
            putS3(resizedImage3, imageName3);

        } catch (Exception e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        } finally {
            deleteFile(localFile);
        }
    }

    // MultipartFile 을 로컬 파일로 변환
    private File convertToFile(MultipartFile multipartFile, String fileName) {
        try {
            File tempFile = File.createTempFile(fileName, FILE_EXTENSION);
            multipartFile.transferTo(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

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

    // 업로드 하기
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
            File resizedFile = new File(file.getParent(), name);
            Thumbnails.of(file)
                    .size(width, height)
                    .outputFormat("jpg")
                    .toFile(resizedFile);
            return resizedFile;
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    private String makeResizedFileName(String name, int width, int height) {
        return name + "_" + width + "x" + height;
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            throw new RuntimeException("썸네일 파일 제거 실패!");
        }
    }
}
