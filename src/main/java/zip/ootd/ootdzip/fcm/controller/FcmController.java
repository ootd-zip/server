package zip.ootd.ootdzip.fcm.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.fcm.data.FcmPostConfigReq;
import zip.ootd.ootdzip.fcm.data.FcmPostReq;
import zip.ootd.ootdzip.fcm.service.FcmService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "FCM 컨트롤러", description = "푸쉬 알람 설정할 때 사용 합니다.")
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;

    private final UserService userService;

    /**
     * 푸쉬 알람을 허용한 유저로부터
     * FCM 토큰값을 얻어와서 DB 에 저장해둡니다.
     * 해당 토큰값은 디바이스 고유값으로 해당 값으로 FCM 이 디바이스에게 푸쉬알람을 보낼 수 있습니다.
     * 유저가 앱을실행하고 로그인할 때마다 프론트는 해당 API 를통해 토큰값을 서버로 보냅니다.
     */
    @Operation(summary = "FCM 토큰 등록", description = "해당 유저가 최초 호출시 DB 등록 및 세팅, 두번째 호출시는 로그인상태로 변경합니다.")
    @PostMapping("")
    public ApiResponse<Boolean> onFcmToken(@RequestBody @Valid FcmPostReq fcmPostReq) {

        fcmService.onFcmToken(fcmPostReq, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    /**
     * 사용자 토큰 상태를 off 하여 해당 기기는 알림을 받을 수 없습니다.
     */
    @Operation(summary = "FCM 토큰 로그아웃", description = "FCM 토큰 상태를 로그아웃 상태로 변경하여 알람을 수신받지 않습니다.")
    @DeleteMapping("")
    public ApiResponse<Boolean> offFcmToken(@RequestBody @Valid FcmPostReq fcmPostReq) {

        fcmService.offFcmToken(fcmPostReq);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "FCM 토큰 설정 변경", description = "전체 알람 허용, 세부 알람 허용을 설정할 수 있습니다.")
    @PostMapping("/config")
    public ApiResponse<Boolean> changeConfig(@RequestBody @Valid FcmPostConfigReq fcmPostConfigReq) {

        fcmService.changeConfig(fcmPostConfigReq);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "FCM 토큰 설정 조회", description = "현재 설정된 전체알람, 세부알람 정보를 가져옵니다.")
    @GetMapping("/config/{token}")
    public ApiResponse<Boolean> getConfig(@PathVariable String token) {

        fcmService.getConfig(token);

        return new ApiResponse<>(true);
    }
}
