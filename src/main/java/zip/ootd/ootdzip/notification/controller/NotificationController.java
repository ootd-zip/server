package zip.ootd.ootdzip.notification.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.notification.service.NotificationService;
import zip.ootd.ootdzip.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "알람 컨트롤러", description = "알람 관련 api")
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    @Hidden
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(userService.getAuthenticatiedUser(), lastEventId);
    }
}
