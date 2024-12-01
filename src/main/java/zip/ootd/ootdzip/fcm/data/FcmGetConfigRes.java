package zip.ootd.ootdzip.fcm.data;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;
import zip.ootd.ootdzip.fcm.domain.FcmInfo;
import zip.ootd.ootdzip.fcm.domain.FcmNotificationType;
import zip.ootd.ootdzip.notification.domain.NotificationType;

@Data
public class FcmGetConfigRes {

    Boolean isPermission;

    List<DetailNotification> detailNotifications;

    @Data
    static class DetailNotification {

        NotificationType notificationType;

        Boolean isAllow;

        DetailNotification(FcmNotificationType fcmNotificationType) {
            this.notificationType = fcmNotificationType.getNotificationType();
            this.isAllow = fcmNotificationType.getIsAllow();
        }
    }

    FcmGetConfigRes(FcmInfo fcmInfo) {

        isPermission = fcmInfo.getIsPermission();

        this.detailNotifications = fcmInfo.getFcmNotificationTypes().stream()
                .map(DetailNotification::new)
                .collect(Collectors.toList());
    }
}
