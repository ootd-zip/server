package zip.ootd.ootdzip.images.controller;

import java.util.List;
import java.util.stream.Collectors;

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
@RequestMapping("/api/v1")
@Slf4j
public class ImagesController {

    private final ImagesService imagesService;

    /**
     * 프론트에는 사진 url 을 만들어서 먼저 반환해줍니다.(실제로 이미지가 저장되지 않은상태)
     * 서블릿 컨테이너 디스크에 먼저 MultipartFile 이 저장되지만 이는 쓰레드별로 저장되어 다른 쓰레드가 접근할 수 없다.
     * 그래서 비동기로 접근시 파일이 없다는 에러가 떠서 그전에 물리적인 파일로 저장해준다.
     * 비동기로 s3 로 이미지를 저장합니다.
     * 비동기로 실행해서 자가호출문제를 막기위해 컨트롤러에서 imageService 를 따로 호출합니다.
     */
    @Operation(summary = "s3 이미지 저장", description = "사용자가 올릴 이미지를 s3에 저장, 해당 url 을 반환 합니다.")
    @PostMapping(value = "/s3/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiResponse<ImagesRes> saveS3Image(@ModelAttribute ImagesReq request) {

        List<String> imageUrls = request.getImages().stream()
                .map(i -> {
                    String fileName = imagesService.makeFileName();
                    imagesService.upload(imagesService.convertToFile(i, fileName), fileName);
                    return imagesService.makeImageUrl(fileName);
                })
                .collect(Collectors.toList());

        ImagesRes imagesRes = new ImagesRes(imageUrls);

        return new ApiResponse<>(imagesRes);
    }
}
