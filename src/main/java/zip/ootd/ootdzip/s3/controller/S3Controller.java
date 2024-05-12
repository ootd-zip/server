package zip.ootd.ootdzip.s3.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.s3.data.S3ImageReq;
import zip.ootd.ootdzip.s3.data.S3ImageRes;
import zip.ootd.ootdzip.s3.service.S3UploadService;

@RestController
@RequiredArgsConstructor
@Tag(name = "S3 컨트롤러", description = "이미지를 S3 에 저장할때 호출합니다")
@RequestMapping("/api/v1/s3")
@Slf4j
public class S3Controller {

    private final S3UploadService s3UploadService;

    @Operation(summary = "s3 이미지 저장", description = "사용자가 올릴 이미지를 s3에 저장후 해당 url 을 반환합니다.")
    @PostMapping(value = "/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<S3ImageRes> saveS3Image(@ModelAttribute S3ImageReq request) {
        List<String> images = s3UploadService.saveImageS3(request);
        S3ImageRes s3ImageRes = new S3ImageRes(images);

        return new ApiResponse<>(s3ImageRes);
    }
}
