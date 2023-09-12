package zip.ootd.ootdzip.clothes.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zip.ootd.ootdzip.clothes.data.ClothesResponseDto;
import zip.ootd.ootdzip.clothes.data.SaveClothesDto;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Clothes 컨트롤러", description = "옷 관련 API입니다.")
@RequestMapping("/api/v1/clothes")
public class ClothesController {

    @Autowired
    private final ClothesService clothesService;

    @Operation(summary = "옷 저장 API", description = "옷 저장 API")
    @PostMapping("/")
    public ApiResponse<ClothesResponseDto> saveClothes(@RequestBody SaveClothesDto saveClothesDto){
        ClothesResponseDto clothesResponseDto = null;
        clothesResponseDto = clothesService.saveClothes(saveClothesDto);
        return new ApiResponse<ClothesResponseDto>(clothesResponseDto);
    }

}
