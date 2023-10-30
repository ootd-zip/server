package zip.ootd.ootdzip.home.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.home.data.GetClothesAndOotdsForHomeRes;
import zip.ootd.ootdzip.home.service.HomeService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Home 컨트롤러", description = "Home 관련 api")
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "홈 - 유저 프로필 정보 조회", description = "유저 프로필 정보 조회 API")
    @GetMapping("/profile")
    public ApiResponse<List<GetClothesAndOotdsForHomeRes>> getProfile() {
        return new ApiResponse<>(homeService.GetClothesAndOotdsForHomeRes());
    }
}
