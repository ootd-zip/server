package zip.ootd.ootdzip.ootdbookmark.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.request.CommonPageRequest;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkDeleteReq;
import zip.ootd.ootdzip.ootdbookmark.data.OotdBookmarkGetAllRes;
import zip.ootd.ootdzip.ootdbookmark.repository.OotdBookmarkRepository;
import zip.ootd.ootdzip.ootdbookmark.service.OotdBookmarkService;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "OotdBookmark 컨트롤러", description = "ootdBookmark 관련 api")
@RequestMapping("/api/v1/")
public class OotdBookmarkController {

    private final OotdBookmarkService ootdBookmarkService;
    private final OotdBookmarkRepository ootdBookmarkRepository;
    private final UserService userService;

    @Operation(summary = "bookmark 전체조회", description = "사용자 기준 북마크 전체조회 기능")
    @GetMapping("/bookmarks")
    public ApiResponse<CommonSliceResponse<OotdBookmarkGetAllRes>> getBookmarks(CommonPageRequest request) {
        User loginUser = userService.getAuthenticatiedUser();

        CommonSliceResponse<OotdBookmarkGetAllRes> response = ootdBookmarkService.getOotdBookmarks(loginUser,
                request);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "bookmark 선택삭제", description = "선태된 북마크들 삭제 기능")
    @DeleteMapping("/bookmarks")
    public ApiResponse<Boolean> getBookmarks(@Valid OotdBookmarkDeleteReq request) {

        ootdBookmarkRepository.deleteAllById(request.getOotdBookmarkIds());

        return new ApiResponse<>(true);
    }
}
