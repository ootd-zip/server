package zip.ootd.ootdzip.userblock.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.service.UserBlockService;
import zip.ootd.ootdzip.userblock.service.request.BlockUserSvcReq;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Block 컨트롤러", description = "사용자 차단 기능 관련 API")
@RequestMapping("/api/v1/user-block")
@Validated
public class UserBlockController {

    private final UserBlockService userBlockService;
    private final UserService userService;

    @PostMapping("/{userId}")
    public ApiResponse<String> blockUser(
            @PathVariable(name = "userId") @Positive(message = "유저 ID는 양수여야 합니다.") Long userId) {
        userBlockService.blockUser(BlockUserSvcReq.createBy(userId), userService.getAuthenticatiedUser());
        return new ApiResponse<>("성공");
    }
}
