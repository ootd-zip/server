package zip.ootd.ootdzip.fcm.data;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.notification.domain.NotificationType;

@Data
public class FcmPostConfigReq {

    @NotNull(message = "토큰값은 필수입니다.")
    private String fcmToken;

    private Boolean isPermission;

    List<DetailNotification> detailNotifications;

    @Data
    public static class DetailNotification {

        private NotificationType notificationType;

        private Boolean isAllow;
    }
}
