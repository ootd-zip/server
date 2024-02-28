package zip.ootd.ootdzip.s3.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.s3.data.S3ImageReq;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3UploadService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> saveImageS3(S3ImageReq request) {
        List<MultipartFile> images = request.getImages();
        checkFilesSize(images);

        return images.stream()
                .map(this::upload)
                .collect(Collectors.toList());
    }

    // s3로 파일 업로드하기
    private String upload(MultipartFile multipartFile) {
        String fileName = makeFileName(multipartFile);
        return putS3(multipartFile, fileName);
    }

    // 업로드하기
    private String putS3(MultipartFile multipartFile, String fileName) {
        try {
            amazonS3Client.putObject(
                    new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), null)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_CONVERT_ERROR);
        }
    }

    private String makeFileName(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        String fileExtension = Objects.requireNonNull(originalFileName).substring(originalFileName.lastIndexOf("."));

        return UUID.randomUUID() + "_" + LocalDate.now() + fileExtension;
    }

    private void checkFilesSize(List<MultipartFile> multipartFiles) {
        long sum = multipartFiles.stream()
                .mapToLong(this::checkFileSize)
                .sum();
        if (sum > 1024 * 1024 * 5) {
            throw new IllegalArgumentException("사진 총 크기가 50MB 를 넘었습니다. 보낸 사진 총 크기 : " + sum + "bytes");
        }
    }

    private Long checkFileSize(MultipartFile multipartFile) {
        long size = multipartFile.getSize();
        if (size > 1024 * 1024) {
            throw new IllegalArgumentException("사진 크기가 10MB 를 넘었습니다. 보낸 사진 크기 : " + size + "bytes");
        }
        return size;
    }
}
