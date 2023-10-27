package zip.ootd.ootdzip.ootd.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.ootd.data.OotdGetAllRes;
import zip.ootd.ootdzip.ootd.data.OotdGetRes;
import zip.ootd.ootdzip.ootd.data.OotdPostReq;
import zip.ootd.ootdzip.ootd.data.OotdPostRes;
import zip.ootd.ootdzip.ootd.service.OotdService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ootd 컨트롤러", description = "ootd 관련 api")
@RequestMapping("/api/v1/ootd")
public class OotdController {

    private final OotdService ootdService;

    @Operation(summary = "ootd 작성", description = "사용자가 작성한 ootd 글을 저장하는 api")
    @PostMapping("/")
    public ApiResponse<OotdPostRes> saveOotdPost(@RequestBody @Valid OotdPostReq request) {

        OotdPostRes response = new OotdPostRes(ootdService.postOotd(request));

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 조회", description = "ootd id 를 주면 해당 id에 해당하는 ootd 반환 api")
    @GetMapping("/{id}")
    public ApiResponse<OotdGetRes> getOotdPost(@PathVariable Long id) {

        OotdGetRes response = ootdService.getOotd(id);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 전체 조회", description = "최신순으로 ootd를 조회 api")
    @GetMapping("/all")
    public ApiResponse<List<OotdGetAllRes>> getOotdPosts() {

        List<OotdGetAllRes> response = ootdService.getOotds();

        return new ApiResponse<>(response);
    }

    @Operation(summary = "ootd 좋아요 추가", description = "특정 ootd에 좋아요를 추가합니다.")
    @PostMapping("/like/{id}")
    public ApiResponse<Boolean> addLike(@PathVariable Long id) {

        ootdService.addLike(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 좋아요 제거", description = "특정 ootd에 좋아요를 취소합니다.")
    @DeleteMapping("/like/{id}")
    public ApiResponse<Boolean> cancelLike(@PathVariable Long id) {

        ootdService.cancelLike(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 북마크 추가", description = "특정 ootd에 북마크를 추가합니다.")
    @PostMapping("/bookmark/{id}")
    public ApiResponse<Boolean> addBookMark(@PathVariable Long id) {

        ootdService.addBookmark(id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "ootd 북마크 제거", description = "특정 ootd에 북마크를 취소합니다.")
    @DeleteMapping("/bookmark/{id}")
    public ApiResponse<Boolean> cancelBookMark(@PathVariable Long id) {

        ootdService.cancelBookmark(id);

        return new ApiResponse<>(true);
    }
}