package zip.ootd.ootdzip.notification.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class NotificationRes {

    Long notificationId;

    String userProfileImage;

    String timeStamp;

    Boolean isRead;

    String content;

    String ootdImage;

    //TODO : 푸쉬 알림 필요시 해당 DTO 사용
}
