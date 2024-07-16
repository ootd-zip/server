package zip.ootd.ootdzip.brandrequest.controller.reqeuest;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.brandrequest.service.request.BrandRequestSvcReq;

@Setter
@Getter
@NoArgsConstructor
public class BrandRequestReq {

    @NotBlank(message = "브랜드 요청 내용은 필수입니다.")
    private String requestContents;

    public BrandRequestSvcReq toServiceRequest() {
        return BrandRequestSvcReq.builder()
                .requestContents(this.requestContents)
                .build();
    }
}
