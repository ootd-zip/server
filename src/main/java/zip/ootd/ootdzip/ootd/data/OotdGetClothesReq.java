package zip.ootd.ootdzip.ootd.data;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class OotdGetClothesReq extends CommonPageRequest {

    @NotNull(message = "해당 옷을 이용한 OOTD 는 옷 id가 필수입니다.")
    private Long clothesId;
}
