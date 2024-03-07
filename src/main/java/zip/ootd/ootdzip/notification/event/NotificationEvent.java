package zip.ootd.ootdzip.notification.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.user.domain.User;

@Builder
@AllArgsConstructor
@Getter
public class NotificationEvent {

    private User receiver;

    private User sender;

    private NotificationType notificationType;

    // 푸쉬 알람 사용시 이동할 url 링크
    private String goUrl;

    // 해당 content 는 현재 댓글 알람에서만 사용됩니다.
    @Builder.Default
    private String content = "";

    // 해당 imageUrl 은 현재 ootd 좋아요, 댓글 알림에 사용됩니다.
    @Builder.Default
    private String imageUrl = "";
}

