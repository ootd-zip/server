package zip.ootd.ootdzip.home.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.home.data.ClothesAndOotdsForHomeRes;
import zip.ootd.ootdzip.home.data.SameClothesDifferentFeelRes;
import zip.ootd.ootdzip.home.service.HomeService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Home 컨트롤러", description = "Home 관련 api")
@RequestMapping("/api/v1/home")
public class HomeController {

    private final HomeService homeService;
    private final UserService userService;

    @Operation(summary = "홈 - 유저 프로필 정보 조회", description = "유저 프로필 정보 조회 API")
    @GetMapping("/profile")
    public ApiResponse<List<ClothesAndOotdsForHomeRes>> getProfile() {
        return new ApiResponse<>(homeService.getClothesAndOotdsForHomeRes());
    }

    @Operation(summary = "홈 - 같은 옷 다른 느낌 조회", description = "같은 옷 다른 느낌 조회하는 API")
    @GetMapping("/scdf") // SameClothesDifferentFeel
    public ApiResponse<List<SameClothesDifferentFeelRes>> getSameClothesDifferentFeel() {
        return new ApiResponse<>(homeService.getSameClothesDifferentFeel(userService.getAuthenticatiedUser()));
    }
}
