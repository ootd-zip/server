package zip.ootd.ootdzip.report.controller.request;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.report.service.request.ReportOotdSvcReq;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportOotdReq {

    @Positive(message = "신고 항목 선택은 필수입니다.")
    private Long reportId;

    @Positive(message = "신고할 ootd 선택은 필수입니다.")
    private Long ootdId;

    public ReportOotdSvcReq toServiceReq() {
        return ReportOotdSvcReq.builder()
                .reportId(this.reportId)
                .ootdId(this.ootdId)
                .build();
    }

}
