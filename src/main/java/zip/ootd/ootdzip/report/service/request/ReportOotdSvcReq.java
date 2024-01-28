package zip.ootd.ootdzip.report.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportOotdSvcReq {

    private final Long reportId;
    private final Long ootdId;

    @Builder
    private ReportOotdSvcReq(Long reportId, Long ootdId) {
        this.reportId = reportId;
        this.ootdId = ootdId;
    }

    public static ReportOotdSvcReq of(Long reportId, Long ootdId) {
        return ReportOotdSvcReq.builder()
                .reportId(reportId)
                .ootdId(ootdId)
                .build();
    }
}
