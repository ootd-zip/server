package zip.ootd.ootdzip.report.service.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReportSvcReq {

    private final Long reportId;
    private final Long targetId;
    private final ReportType reportType;

    @Builder
    private ReportSvcReq(Long reportId, Long targetId, ReportType reportType) {
        this.reportId = reportId;
        this.targetId = targetId;
        this.reportType = reportType;
    }

    public static ReportSvcReq of(Long reportId, Long targetId, ReportType reportType) {
        return ReportSvcReq.builder()
                .reportId(reportId)
                .targetId(targetId)
                .reportType(reportType)
                .build();
    }
}
