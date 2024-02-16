package zip.ootd.ootdzip.user.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.user.data.CheckNameReq;
import zip.ootd.ootdzip.user.data.FollowReq;
import zip.ootd.ootdzip.user.data.ProfileRes;
import zip.ootd.ootdzip.user.data.TokenUserInfoRes;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.data.UserRegisterReq;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
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
    public void register(@RequestBody UserRegisterReq request) {
        userService.register(request);
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

    @GetMapping("/check-name")
    public ApiResponse<Boolean> checkName(@RequestBody CheckNameReq request) {
        return new ApiResponse<>(userService.checkName(request.getName()));
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

    @GetMapping("/profile")
    public ProfileRes getProfile() {
        return new ProfileRes(userService.getAuthenticatiedUser());
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

}
