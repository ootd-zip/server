package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class OotdGetByUserReq extends CommonPageRequest {

    @NotNull(message = "마이페이지 ootd 조회시 조회할 user id 는 필수입니다.")
    private Long userId;
}
