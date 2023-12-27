package zip.ootd.ootdzip.clothes.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.FindClothesByUserReq;
import zip.ootd.ootdzip.clothes.data.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesReq;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.common.response.ApiResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Clothes 컨트롤러", description = "옷 관련 API입니다.")
@RequestMapping("/api/v1/clothes")
public class ClothesController {

    @Autowired
    private final ClothesService clothesService;

    @Operation(summary = "옷 저장", description = "옷 저장 API")
    @PostMapping("")
    public ApiResponse<SaveClothesRes> saveClothes(@RequestBody @Valid SaveClothesReq request) {
        return new ApiResponse<>(new SaveClothesRes(clothesService.saveClothes(request)));
    }

    @Operation(summary = "옷 ID로 조회", description = "옷 조회 API - 옷 ID로 조회")
    @GetMapping("/{id}")
    public ApiResponse<FindClothesRes> findClothesById(@PathVariable Long id) {
        return new ApiResponse<>(clothesService.findClothesById(id));
    }

    @Operation(summary = "유저 옷 리스트 조회", description = "유저 옷 리스트 조회")
    @GetMapping("")
    public ApiResponse<List<FindClothesRes>> findClothesByUser(@RequestParam Long userId) {
        return new ApiResponse<>(
                clothesService.findClothesByUser(FindClothesByUserReq.builder().userId(userId).build()));
    }

    @Operation(summary = "옷 삭제 API", description = "ID로 옷 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<DeleteClothesByIdRes> deleteClothesById(@PathVariable Long id) {
        return new ApiResponse<>(clothesService.deleteClothesById(id));
    }
}
