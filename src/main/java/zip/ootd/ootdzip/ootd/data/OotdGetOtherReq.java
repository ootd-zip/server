package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class OotdGetOtherReq extends CommonPageRequest {

    @NotNull(message = "비슷한 OOTD 조회시 작성자 id 는 필수입니다.")
    private Long userId;

    @NotNull(message = "비슷한 OOTD 조회시 현재 OOTD id 는 필수입니다.")
    private Long ootdId;
}
