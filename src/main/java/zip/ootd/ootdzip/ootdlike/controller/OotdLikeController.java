package zip.ootd.ootdzip.ootdlike.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.ootdlike.controller.response.OotdLikeRes;
import zip.ootd.ootdzip.ootdlike.service.OotdLikeService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Ootd-Like 컨트롤러", description = "ootd-like 관련 api")
@RequestMapping("/api/v1/ootd-like")
public class OotdLikeController {

    private final UserService userService;
    private final OotdLikeService ootdLikeService;

    @Operation(summary = "좋아요한 ootd 조회", description = "로그인한 유저가 좋아요한 ootd 조회 API")
    @GetMapping("")
    public ApiResponse<List<OotdLikeRes>> getUserOotdLikes() {
        return new ApiResponse<>(ootdLikeService.getUserOotdLikes(userService.getAuthenticatiedUser()));
    }
}
