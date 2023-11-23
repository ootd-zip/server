package zip.ootd.ootdzip.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.StyleRes;
import zip.ootd.ootdzip.category.service.StyleService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Style 컨트롤러", description = "Style 관련 API입니다.")
@RequestMapping("/api/v1/color")
public class StyleController {

    private final StyleService styleService;

    @GetMapping("/")
    public ApiResponse<List<StyleRes>> getStyles() {
        return new ApiResponse<>(styleService.getAllStyles());
    }
}
