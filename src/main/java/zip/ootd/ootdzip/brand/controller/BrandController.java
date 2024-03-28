package zip.ootd.ootdzip.brand.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.brand.controller.request.BrandSearchReq;
import zip.ootd.ootdzip.brand.data.BrandDto;
import zip.ootd.ootdzip.brand.data.BrandSaveReq;
import zip.ootd.ootdzip.brand.service.BrandService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Brand 컨트롤러", description = "브랜드 관련 API입니다.")
@RequestMapping("/api/v1/brand")
public class BrandController {

    private final BrandService brandService;
    private final UserService userService;

    @PostMapping
    public ApiResponse<BrandDto> saveBrand(@RequestBody BrandSaveReq request) {
        return new ApiResponse<>(brandService.saveBrand(request));
    }

    @GetMapping
    public ApiResponse<List<BrandDto>> getBrands(BrandSearchReq request) {
        return new ApiResponse<>(brandService.getBrands(request.toServiceRequest()));
    }

    @GetMapping("user/{userId}")
    public ApiResponse<List<BrandDto>> getUserBrands(@PathVariable(name = "userId") Long userId) {
        return new ApiResponse<>(brandService.getUserBrands(userId, userService.getAuthenticatiedUser()));
    }
}
