package zip.ootd.ootdzip.user.controller;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Hidden;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.user.controller.request.ProfileReq;
import zip.ootd.ootdzip.user.controller.request.UserRegisterReq;
import zip.ootd.ootdzip.user.controller.request.UserSearchReq;
import zip.ootd.ootdzip.user.controller.request.UserStyleUpdateReq;
import zip.ootd.ootdzip.user.controller.response.ProfileRes;
import zip.ootd.ootdzip.user.controller.response.UserInfoForMyPageRes;
import zip.ootd.ootdzip.user.controller.response.UserSearchRes;
import zip.ootd.ootdzip.user.controller.response.UserStyleRes;
import zip.ootd.ootdzip.user.data.CheckNameReq;
import zip.ootd.ootdzip.user.data.FollowReq;
import zip.ootd.ootdzip.user.data.TokenUserInfoRes;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.user.service.request.UserInfoForMyPageSvcReq;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
@Tag(name = "User 컨트롤러", description = "유저 관련 API입니다.")
@Validated
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody UserLoginReq request, HttpServletResponse response) {
        TokenInfo info = userService.login(request);
        String refreshToken = info.getRefreshToken();
        info.setRefreshToken("");

        Cookie cookie = createRefreshTokenCookie(refreshToken, info.getRefreshTokenExpiresIn());
        response.addCookie(cookie);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserRegisterReq request) {
        userService.register(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("회원가입 성공");
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refresh(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = findRefreshTokenCookie(request.getCookies());
        if (refreshTokenCookie.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Cookie reqCookie = refreshTokenCookie.get();
        TokenInfo info = userService.refresh(reqCookie.getValue());
        String refreshToken = info.getRefreshToken();
        info.setRefreshToken("");

        Cookie cookie = createRefreshTokenCookie(refreshToken, info.getRefreshTokenExpiresIn());
        response.addCookie(cookie);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @GetMapping("/token/info")
    public ApiResponse<TokenUserInfoRes> userinfo() {
        User currentUser = userService.getAuthenticatiedUser();
        return new ApiResponse<>(userService.getUserInfo(currentUser));
    }

    @PostMapping("/follow")
    public ApiResponse<Boolean> follow(@RequestBody FollowReq request) {
        User currentUser = userService.getAuthenticatiedUser();
        if (userService.follow(request.getUserId(), currentUser.getId())) {
            return new ApiResponse<>(true);
        } else {
            throw new CustomException(ErrorCode.FOLLOW_ERROR);
        }
    }

    @PostMapping("/unfollow")
    public ApiResponse<Boolean> unfollow(@RequestBody FollowReq request) {
        User currentUser = userService.getAuthenticatiedUser();
        if (userService.unfollow(request.getUserId(), currentUser.getId())) {
            return new ApiResponse<>(true);
        } else {
            throw new CustomException(ErrorCode.UNFOLLOW_ERROR);
        }
    }

    /**
     * 현재 사용하지 않는 슬픈 API
     * 미래에 니즈가 생긴다면 풀도록 합니다.
     */
    @Hidden
    @DeleteMapping("/follower")
    public ApiResponse<Boolean> removeFollower(@RequestBody FollowReq request) {
        User loginUser = userService.getAuthenticatiedUser();
        if (userService.removeFollower(loginUser, request)) {
            return new ApiResponse<>(true);
        } else {
            throw new CustomException(ErrorCode.UNFOLLOW_ERROR);
        }
    }

    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkName(CheckNameReq request) {
        return new ApiResponse<>(userService.checkName(request));
    }

    private Cookie createRefreshTokenCookie(String refreshToken, int maxAge) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(maxAge);
        //        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }

    private Optional<Cookie> findRefreshTokenCookie(Cookie[] cookies) {
        if (cookies == null) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("refreshToken")) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    @Operation(summary = "로그인 유저 프로필 정보 조회")
    @GetMapping("/profile")
    public ApiResponse<ProfileRes> getProfile() {
        return new ApiResponse<>(userService.getProfile(userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "로그인 유저 프로필 정보 업데이트")
    @PatchMapping("/profile")
    public ApiResponse<String> updateProfile(@RequestBody @Valid ProfileReq request) {
        userService.updateProfile(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("프로필 정보 업데이트 성공");
    }

    @GetMapping("/nickname")
    public String getName() {
        return userService.getAuthenticatiedUser().getName();
    }

    @GetMapping("/complete")
    public ApiResponse<Boolean> getIsComplete() {
        User currentUser = userService.getAuthenticatiedUser();
        return new ApiResponse<>(currentUser.getIsCompleted());
    }

    @Operation(summary = "유저 마이페이지 정보 조회", description = "유저 마이페이지 정보 조회")
    @GetMapping("/{id}/mypage")
    public ApiResponse<UserInfoForMyPageRes> getUserInfoForMyPage(
            @PathVariable(name = "id") @Positive(message = "유저 ID는 양수여야 합니다.") Long id) {
        return new ApiResponse<>(userService.getUserInfoForMyPage(UserInfoForMyPageSvcReq.createBy(id),
                userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "유저 프로필 검색", description = "유저 프로필 검색 API")
    @GetMapping("/search")
    public ApiResponse<CommonSliceResponse<UserSearchRes>> searchUser(UserSearchReq request) {
        return new ApiResponse<>(
                userService.searchUser(request.toServiceRequest(), userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "유저 스타일 조회", description = "로그인한 유저의 선호 스타일 조회")
    @GetMapping("user-styles")
    public ApiResponse<List<UserStyleRes>> getUserStyles() {
        return new ApiResponse<>(userService.getUserStyle(userService.getAuthenticatiedUser()));
    }

    @Operation(summary = "유저 스타일 업데이트", description = "유저 스타일 업데이트")
    @PutMapping("user-styles")
    public ApiResponse<String> updateUserStyles(@RequestBody @Valid UserStyleUpdateReq request) {
        userService.updateUserStyles(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse("ok");
    }

}
