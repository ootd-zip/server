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

    @NotBlank(message = "요청할 브랜드의 이름은 필수입니다.")
    private String requestName;

    public BrandRequestSvcReq toServiceRequest() {
        return BrandRequestSvcReq.builder()
                .requestName(this.requestName)
                .build();
    }
}
