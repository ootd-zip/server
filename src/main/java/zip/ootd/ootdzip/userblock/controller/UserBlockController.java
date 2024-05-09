package zip.ootd.ootdzip.userblock.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.controller.request.BlockUserReq;
import zip.ootd.ootdzip.userblock.service.UserBlockService;
import zip.ootd.ootdzip.userblock.service.request.UnBlockUserSvcReq;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Block 컨트롤러", description = "사용자 차단 기능 관련 API")
@RequestMapping("/api/v1/user-block")
@Validated
public class UserBlockController {

    private final UserBlockService userBlockService;
    private final UserService userService;

    @PostMapping("")
    public ApiResponse<String> blockUser(@RequestBody @Valid BlockUserReq request) {
        userBlockService.blockUser(request.toServiceReq(), userService.getAuthenticatiedUser());
        return new ApiResponse<>("성공");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> blockUser(@PathVariable(name = "id") @Positive(message = "차단 ID는 양수여야 합니다.") Long id) {
        userBlockService.unBlockUser(UnBlockUserSvcReq.createBy(id), userService.getAuthenticatiedUser());
        return new ApiResponse<>("성공");
    }

}
