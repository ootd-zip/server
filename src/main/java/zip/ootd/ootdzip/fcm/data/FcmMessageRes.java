package zip.ootd.ootdzip.fcm.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import zip.ootd.ootdzip.notification.domain.Notification;

@Getter
@Builder
public class FcmMessageRes {
    private boolean validateOnly;
    private FcmMessageRes.Message message;

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Message {
        private Notification notification;
        private String token;
    }
}
