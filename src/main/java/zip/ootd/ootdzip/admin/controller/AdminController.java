package zip.ootd.ootdzip.admin.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.admin.controller.request.AdminJoinReq;
import zip.ootd.ootdzip.admin.controller.request.AdminLoginReq;
import zip.ootd.ootdzip.admin.service.AdminService;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.oauth.data.TokenResponse;
import zip.ootd.ootdzip.oauth.service.LoginService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin 컨트롤러", description = "어드민 계정 관련 API입니다.")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final LoginService loginService;
    private final UserService userService;

    @PostMapping("/join")
    public ApiResponse<String> join(@Valid @RequestBody AdminJoinReq request) {
        adminService.joinAdmin(request.toServiceRequest(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("OK");
    }

    @PostMapping("login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody AdminLoginReq request) {
        return new ApiResponse<>(loginService.loginForAdmin(request.toServiceRequest()));
    }
}
