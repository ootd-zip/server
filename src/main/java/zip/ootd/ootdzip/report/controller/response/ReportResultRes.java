package zip.ootd.ootdzip.report.controller.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportResultRes {

    private Long id;
    private Integer reportCount;

    @Builder
    private ReportResultRes(Long id, Integer reportCount) {
        this.id = id;
        this.reportCount = reportCount;
    }

    public static ReportResultRes of(Long id, Integer reportCount) {
        return ReportResultRes.builder()
                .id(id)
                .reportCount(reportCount)
                .build();
    }
}
