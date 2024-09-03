package zip.ootd.ootdzip.brandrequest.controller.reqeuest;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestRejectSvcReq;

@Getter
@NoArgsConstructor
public class BrandRequestRejectReq {

    @NotEmpty(message = "브랜드 요청 ID는 필수입니다.")
    private List<Long> brandRequestId;

    @NotBlank(message = "거절 사유는 필수입니다.")
    private String reason;

    public BrandRequestRejectSvcReq toServiceRequest() {
        return BrandRequestRejectSvcReq.builder()
                .brandRequestId(brandRequestId)
                .reason(reason)
                .build();
    }
}
