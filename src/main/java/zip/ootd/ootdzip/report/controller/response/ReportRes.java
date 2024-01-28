package zip.ootd.ootdzip.report.controller.response;

import lombok.Getter;
import zip.ootd.ootdzip.report.domain.Report;

@Getter
public class ReportRes {

    private final Long id;

    private final String message;

    private ReportRes(Long id, String message) {
        this.id = id;
        this.message = message;
    }

    public static ReportRes of(Report report) {
        return new ReportRes(report.getId(), report.getMessage());
    }

}
