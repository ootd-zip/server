package zip.ootd.ootdzip.report.controller.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.valid.EnumValid;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.report.service.request.ReportType;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportReq {

    @NotEmpty(message = "신고항목은 필수입니다.")
    private List<@Positive(message = "신고 항목 ID는 양수입니다.") Long> reportIds;

    @Positive(message = "신고할 ootd 선택은 필수입니다.")
    private Long targetId;

    @EnumValid(enumClass = ReportType.class, message = "유효하지 않은 신고타입입니다.")
    private ReportType reportType;

    public ReportSvcReq toServiceReq() {
        return ReportSvcReq.builder()
                .reportIds(this.reportIds)
                .targetId(this.targetId)
                .reportType(this.reportType)
                .build();
    }

}
