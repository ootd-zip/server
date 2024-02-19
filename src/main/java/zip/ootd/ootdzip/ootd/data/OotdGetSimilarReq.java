package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class OotdGetSimilarReq extends CommonPageRequest {

    @NotNull(message = "비슷한 OOTD 조회시 현재 ootdId 는 필수입니다.")
    private Long ootdId;
}
