package zip.ootd.ootdzip.clothes.data;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import zip.ootd.ootdzip.common.request.CommonPageRequest;

@Data
public class ClothesOotdReq extends CommonPageRequest {

    @Positive(message = "OOTD ID는 양수여야 합니다.")
    private Long ootdId;

    @Positive(message = "USER ID는 양수여야 합니다.")
    private Long userId;
}
