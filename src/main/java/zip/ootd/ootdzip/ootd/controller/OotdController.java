package zip.ootd.ootdzip.ootd.controller;

import org.springframework.data.domain.SliceImpl;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootd.data.OotdGetAllRes;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherReq;
import zip.ootd.ootdzip.ootd.data.OotdGetOtherRes;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarReq;
import zip.ootd.ootdzip.ootd.data.OotdGetSimilarRes;
import zip.ootd.ootdzip.ootd.data.OotdPatchReq;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPostRes;
import zip.ootd.ootdzip.ootd.data.OotdPutReq;
import zip.ootd.ootdzip.ootd.service.OotdService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ootd 컨트롤러", description = "ootd 관련 api")
@RequestMapping("/api/v1/ootd")
public class OotdController {

    private final OotdService ootdService;

    private final UserService userService;

    @Operation(summary = "ootd 작성", description = "사용자가 작성한 ootd 글을 저장하는 api")
    @PostMapping("")
    public ApiResponse<OotdPostRes> saveOotdPost(@RequestBody @Valid OotdPostReq request) {

        OotdPostRes response = new OotdPostRes(ootdService.postOotd(request, userService.getAuthenticatiedUser()));

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 공개/비공개 여부 수정", description = "ootd 공개여부만 수정하는 api")
    @PatchMapping("/{id}")
    public ApiResponse<Boolean> updateOotdContentsAndIsPrivate(@PathVariable Long id,
            @RequestBody @Valid OotdPatchReq request) {

        ootdService.updateContentsAndIsPrivate(id ,request);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 전체 수정", description = "ootd 게시글 전체 수정 api")
    @PutMapping("/{id}")
    public ApiResponse<Boolean> updateOotdAll(@PathVariable Long id,
            @RequestBody @Valid OotdPutReq request) {

        ootdService.updateAll(id, request);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 삭제", description = "ootd 게시글 삭제하는 api 입니다.")
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteOotd(@PathVariable Long id) {

        ootdService.deleteOotd(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 조회", description = "ootd id 를 주면 해당 id에 해당하는 ootd 반환 api")
    @GetMapping("/{id}")
    public ApiResponse<OotdGetRes> getOotdPost(@PathVariable Long id) {

        OotdGetRes response = ootdService.getOotd(id, userService.getAuthenticatiedUser());

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 전체 조회", description = "최신순으로 ootd를 조회 api")
    @GetMapping("/all")
    public ApiResponse<SliceImpl<OotdGetAllRes>> getOotdPosts(@RequestParam("page") Integer page) {

        SliceImpl<OotdGetAllRes> response = ootdService.getOotds(userService.getAuthenticatiedUser(), page);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 좋아요 추가", description = "특정 ootd에 좋아요를 추가합니다.")
    @PostMapping("/like/{id}")
    public ApiResponse<Boolean> addLike(@PathVariable Long id) {

        ootdService.addLike(id, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 좋아요 제거", description = "특정 ootd에 좋아요를 취소합니다.")
    @DeleteMapping("/like/{id}")
    public ApiResponse<Boolean> cancelLike(@PathVariable Long id) {

        ootdService.cancelLike(id, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 북마크 추가", description = "특정 ootd에 북마크를 추가합니다.")
    @PostMapping("/bookmark/{id}")
    public ApiResponse<Boolean> addBookMark(@PathVariable Long id) {

        ootdService.addBookmark(id, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 북마크 제거", description = "특정 ootd에 북마크를 취소합니다.")
    @DeleteMapping("/bookmark/{id}")
    public ApiResponse<Boolean> cancelBookMark(@PathVariable Long id) {

        ootdService.cancelBookmark(id, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 작성자의 다른 ootd",
            description = "상세페이지에서 ootdId 와 writerId 를 주면 해당 작성자의 다른 ootd 사진을 제공합니다.")
    @GetMapping("/other")
    public ApiResponse<CommonSliceResponse<OotdGetOtherRes>> getOtherOotd(@Valid OotdGetOtherReq request) {

        CommonSliceResponse<OotdGetOtherRes> response = ootdService.getOotdOther(request);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "현재 ootd 와 비슷한 ootd",
            description = "ootd 상세페이지와 동일한 스타일의 ootd 사진 정보 제공합니다.")
    @GetMapping("/similar")
    public ApiResponse<CommonSliceResponse<OotdGetSimilarRes>> getSimilarOotd(@Valid OotdGetSimilarReq request) {

        CommonSliceResponse<OotdGetSimilarRes> response = ootdService.getOotdSimilar(request);

        return new ApiResponse<>(response);
    }
}
