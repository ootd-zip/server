package zip.ootd.ootdzip.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.data.UserRegisterReq;
import zip.ootd.ootdzip.user.service.UserService;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenInfo> login(@RequestBody UserLoginReq request, HttpServletResponse response) {
        TokenInfo info = userService.login(request);
        String refreshToken = info.getRefreshToken();
        info.setRefreshToken("");

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(info.getRefreshTokenExpiresIn());
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PostMapping("/register")
    public void register(@RequestBody UserRegisterReq request) {
        userService.register(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenInfo> refresh(HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> refreshTokenCookie = getRefreshTokenCookie(request.getCookies());
        if (refreshTokenCookie.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Cookie reqCookie = refreshTokenCookie.get();
        TokenInfo info = userService.refresh(reqCookie.getValue());
        String refreshToken = info.getRefreshToken();
        info.setRefreshToken("");

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(info.getRefreshTokenExpiresIn());
//        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);

        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    private Optional<Cookie> getRefreshTokenCookie(Cookie[] cookies) {
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
}
