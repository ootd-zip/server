package zip.ootd.ootdzip.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zip.ootd.ootdzip.oauth.data.TokenInfo;
import zip.ootd.ootdzip.user.data.UserLoginReq;
import zip.ootd.ootdzip.user.data.UserRegisterReq;
import zip.ootd.ootdzip.user.service.UserService;

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
}
