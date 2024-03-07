package zip.ootd.ootdzip.notification.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class NotificationGetAllReq extends CommonPageRequest {

    @NotNull(message = "읽음 알림, 읽지 않음 알림을 조회하는지 알려주어야 합니다.")
    private Boolean isRead;
}
