package zip.ootd.ootdzip.user.controller;

import java.util.List;

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
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonPageResponse;
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

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody @Valid UserRegisterReq request) {
        userService.register(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("회원가입 성공");
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

    @GetMapping("/id")
    public ApiResponse<Long> id() {
        return new ApiResponse<>(userService.getUserId());
    }

    @GetMapping("/social-login-provider")
    public ApiResponse<String> socialLoginProvider() {
        return new ApiResponse<>(userService.getUserSocialLoginProvider());
    }

    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkName(CheckNameReq request) {
        return new ApiResponse<>(userService.checkName(request));
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
    public ApiResponse<CommonPageResponse<UserSearchRes>> searchUser(UserSearchReq request) {
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

    @Operation(summary = "계정 탈퇴", description = "계정 탈퇴 API")
    @DeleteMapping()
    public ApiResponse<String> deleteUser() {
        userService.deleteUser(userService.getAuthenticatiedUser());
        return new ApiResponse<>("ok");
    }

}
