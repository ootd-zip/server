package zip.ootd.ootdzip.fcm.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
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
    @PostMapping("")
    public ApiResponse<Boolean> onFcmToken(@RequestBody @Valid FcmPostReq fcmPostReq) {

        fcmService.onFcmToken(fcmPostReq, userService.getAuthenticatiedUser());

        return new ApiResponse<>(true);
    }

    /**
     * 사용자 토큰 상태를 off 하여 해당 기기는 알림을 받을 수 없습니다.
     */
    @DeleteMapping("")
    public ApiResponse<Boolean> offFcmToken(@RequestBody @Valid FcmPostReq fcmPostReq) {

        fcmService.offFcmToken(fcmPostReq);

        return new ApiResponse<>(true);
    }
}
