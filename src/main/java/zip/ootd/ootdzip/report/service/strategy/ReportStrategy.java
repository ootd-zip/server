package zip.ootd.ootdzip.report.service.strategy;

import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@FunctionalInterface
public interface ReportStrategy {

    ReportResultRes report(final User reporter, final ReportSvcReq request);

    default Report validateReportId(final Long reportId, final ReportRepository reportRepository) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT_ID));
    }

    default void checkReporterAndWriter(final User reporter, final User writer) {
        if (reporter.getId().equals(writer.getId())) {
            throw new CustomException(ErrorCode.CANT_MY_REPORT);
        }
    }
}
