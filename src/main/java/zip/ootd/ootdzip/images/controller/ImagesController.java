package zip.ootd.ootdzip.images.controller;

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
import zip.ootd.ootdzip.images.data.ImagesReq;
import zip.ootd.ootdzip.images.data.ImagesRes;
import zip.ootd.ootdzip.images.service.ImagesService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Images 컨트롤러", description = "이미지를 저장할때 호출합니다")
@RequestMapping("/api/v1/images")
@Slf4j
public class ImagesController {

    private final ImagesService imagesService;

    @Operation(summary = "s3 이미지 저장", description = "사용자가 올릴 이미지를 s3에 저장후 해당 url 을 반환 합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ImagesRes> saveS3Image(@ModelAttribute ImagesReq request) {
        //imagesService.saveImagesToS3(request);
        List<String> images = imagesService.getUrls(request);
        ImagesRes imagesRes = new ImagesRes(images);

        return new ApiResponse<>(imagesRes);
    }
}
