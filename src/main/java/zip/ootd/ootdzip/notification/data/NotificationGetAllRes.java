package zip.ootd.ootdzip.notification.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import zip.ootd.ootdzip.common.util.TimeUtil;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.user.domain.User;

@Builder
@AllArgsConstructor
@Data
public class NotificationGetAllRes {

    private Long id;

    private String profileImage;

    private String userName;

    private String alarmType;

    private String message;

    private String content;

    private String alarmImage;

    private String timeStamp;

    private String timeType;

    public static NotificationGetAllRes of(Notification notification) {

        User receiver = notification.getReceiver();

        return NotificationGetAllRes.builder()
                .id(notification.getId())
                .profileImage(receiver.getProfileImage())
                .userName(receiver.getName())
                .alarmType(notification.getNotificationType().toString())
                .message(notification.getNotificationType().getBaseMessage())
                .content(notification.getContent())
                .alarmImage(notification.getImageUrl())
                .timeStamp(TimeUtil.compareCreatedTimeAndNow(notification.getCreatedAt()))
                .timeType(notification.compareSimpleCreatedTimeAndNow())
                .build();
    }
}
