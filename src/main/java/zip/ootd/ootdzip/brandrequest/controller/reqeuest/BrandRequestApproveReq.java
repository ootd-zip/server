package zip.ootd.ootdzip.brandrequest.controller.reqeuest;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestApproveSvcReq;

@Getter
@NoArgsConstructor
public class BrandRequestApproveReq {

    @NotEmpty(message = "브랜드 요청 ID는 필수입니다.")
    private List<Long> brandRequestId;

    @NotBlank(message = "브랜드명은 필수입니다.")
    private String brandName;

    @NotBlank(message = "브랜드 영문명은 필수입니다.")
    private String brandEngName;

    public BrandRequestApproveSvcReq toServiceRequest() {
        return BrandRequestApproveSvcReq.builder()
                .brandRequestId(brandRequestId)
                .brandName(brandName)
                .brandEngName(brandEngName)
                .build();
    }

}
