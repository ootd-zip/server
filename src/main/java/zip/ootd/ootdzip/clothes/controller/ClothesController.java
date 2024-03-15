package zip.ootd.ootdzip.clothes.controller;

import org.springframework.data.domain.Slice;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.clothes.controller.request.SaveClothesReq;
import zip.ootd.ootdzip.clothes.controller.request.SearchClothesReq;
import zip.ootd.ootdzip.clothes.controller.request.UpdateClothesIsPrivateReq;
import zip.ootd.ootdzip.clothes.controller.request.UpdateClothesReq;
import zip.ootd.ootdzip.clothes.controller.response.FindClothesRes;
import zip.ootd.ootdzip.clothes.data.DeleteClothesByIdRes;
import zip.ootd.ootdzip.clothes.data.SaveClothesRes;
import zip.ootd.ootdzip.clothes.service.ClothesService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Clothes 컨트롤러", description = "옷 관련 API입니다.")
@RequestMapping("/api/v1/clothes")
@Validated
public class ClothesController {

    private final ClothesService clothesService;
    private final UserService userService;

    @Operation(summary = "옷 저장", description = "옷 저장 API")
    @PostMapping("")
    public ApiResponse<SaveClothesRes> saveClothes(@Valid @RequestBody SaveClothesReq request) {
        return new ApiResponse<>(
                clothesService.saveClothes(request.toServiceRequest(), userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "옷 ID로 조회", description = "옷 조회 API - 옷 ID로 조회")
    @GetMapping("/{id}")
    public ApiResponse<FindClothesRes> findClothesById(
            @PathVariable(name = "id") @Positive(message = "옷 ID는 양수여야 합니다.") Long id) {
        return new ApiResponse<>(clothesService.findClothesById(id, userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "유저 옷 리스트 조회", description = "유저 옷 리스트 조회")
    @GetMapping("")
    public ApiResponse<Slice<FindClothesRes>> findClothesByUser(@Valid SearchClothesReq request) {
        return new ApiResponse<>(
                clothesService.findClothesByUser(request.toServiceRequest(),
                        userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "옷 삭제 API", description = "ID로 옷 삭제")
    @DeleteMapping("/{id}")
    public ApiResponse<DeleteClothesByIdRes> deleteClothesById(
            @PathVariable @Positive(message = "옷 ID는 양수여야 합니다.") Long id) {
        return new ApiResponse<>(clothesService.deleteClothesById(id, userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "옷 수정 API", description = "옷 수정")
    @PutMapping("/{id}")
    public ApiResponse<SaveClothesRes> updateClothes(
            @PathVariable(name = "id") @Positive(message = "옷 ID는 양수여야 합니다.") Long id,
            @RequestBody @Valid UpdateClothesReq request) {
        return new ApiResponse<>(
                clothesService.updateClothes(request.toServiceRequest(id), userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "옷 공개여부 수정 API", description = "옷 공개여부 수정")
    @PatchMapping("/{id}")
    public ApiResponse<SaveClothesRes> updateClothesIsPrivate(
            @PathVariable(name = "id") @Positive(message = "옷 ID는 양수여야 합니다.") Long id,
            @RequestBody @Valid UpdateClothesIsPrivateReq request) {
        return new ApiResponse<>(
                clothesService.updateClothesIsPrivate(request.toServiceRequest(id),
                        userService.getAuthenticatiedUser()));
    }

}
