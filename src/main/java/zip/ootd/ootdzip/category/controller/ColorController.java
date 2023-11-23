package zip.ootd.ootdzip.category.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.category.data.ColorRes;
import zip.ootd.ootdzip.category.service.ColorService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Color 컨트롤러", description = "Color 관련 API입니다.")
@RequestMapping("/api/v1/color")
public class ColorController {

    private final ColorService colorService;

    @GetMapping("/")
    public ApiResponse<List<ColorRes>> getColors() {
        return new ApiResponse<>(colorService.getAllColors());
    }
}
