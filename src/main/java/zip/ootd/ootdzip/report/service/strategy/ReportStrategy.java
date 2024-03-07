package zip.ootd.ootdzip.report.service.strategy;

import java.util.List;

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

    default List<Report> validateReportId(final List<Long> reportIds, final ReportRepository reportRepository) {

        List<Report> findReports = reportRepository.findAllById(reportIds);

        if (findReports.size() != reportIds.size()) {
            throw new CustomException(ErrorCode.NOT_FOUND_REPORT_ID);
        }

        return findReports;
    }

    default void checkReporterAndWriter(final User reporter, final User writer) {
        if (reporter.equals(writer)) {
            throw new CustomException(ErrorCode.CANT_MY_REPORT);
        }
    }
}
