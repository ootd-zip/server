package zip.ootd.ootdzip.report.service.request;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReportSvcReq {

    private List<Long> reportIds;
    private Long targetId;
    private ReportType reportType;

    public static ReportSvcReq of(List<Long> reportIds, Long targetId, ReportType reportType) {
        return ReportSvcReq.builder()
                .reportIds(reportIds)
                .targetId(targetId)
                .reportType(reportType)
                .build();
    }
}
