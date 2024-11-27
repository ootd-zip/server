package zip.ootd.ootdzip.fcm.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FcmPostReq {

    @NotNull(message = "토큰값은 필수입니다.")
    private String fcmToken;
}
