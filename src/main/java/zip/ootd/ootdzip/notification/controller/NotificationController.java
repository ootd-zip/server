package zip.ootd.ootdzip.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.ApiResponse;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.notification.data.NotificationGetAllReq;
import zip.ootd.ootdzip.notification.data.NotificationGetAllRes;
import zip.ootd.ootdzip.notification.service.NotificationService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Notification 컨트롤러", description = "알람 관련 api")
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Operation(summary = "알람 조회", description = "사용자가 받은 알람을 조회합니다.")
    @GetMapping(value = "")
    public ApiResponse<CommonSliceResponse<NotificationGetAllRes>> getNotification(
            @Valid NotificationGetAllReq request) {

        CommonSliceResponse<NotificationGetAllRes> response = notificationService.getNotifications(
                userService.getAuthenticatiedUser(), request);

        return new ApiResponse<>(response);
    }

    @Operation(summary = "알람 읽음 수정", description = "알람 읽음으로 처리합니다.")
    @PatchMapping(value = "/{id}")
    public ApiResponse<Boolean> updateIsRead(@PathVariable Long id) {

        notificationService.updateIsRead(userService.getAuthenticatiedUser(), id);

        return new ApiResponse<>(true);
    }

    @Operation(summary = "읽지 않음 알림 존재 유무", description = "읽지 않은 알림이 있는지 확인합니다.")
    @GetMapping(value = "/exist")
    public ApiResponse<Boolean> getIsReadExist() {

        Boolean response = notificationService.getIsReadExist(userService.getAuthenticatiedUser());

        return new ApiResponse<>(response);
    }
}
