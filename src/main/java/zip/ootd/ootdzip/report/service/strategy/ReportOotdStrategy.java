package zip.ootd.ootdzip.report.service.strategy;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportOotd;
import zip.ootd.ootdzip.report.repository.ReportOotdRepository;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@RequiredArgsConstructor
@Component
public class ReportOotdStrategy implements ReportStrategy {

    private final OotdRepository ootdRepository;
    private final ReportOotdRepository reportOotdRepository;
    private final ReportRepository reportRepository;

    @Override
    public ReportResultRes report(User reporter, ReportSvcReq request) {

        Report report = validateReportId(request.getReportId(), reportRepository);

        Ootd ootd = ootdRepository.findById(request.getTargetId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_OOTD_ID));

        checkReporterAndWriter(reporter, ootd.getWriter());

        if (reportOotdRepository.existsByOotdAndReporter(ootd, reporter)) {
            throw new CustomException(ErrorCode.NOT_DUPLICATE_REPORT);
        }

        ReportOotd reportOotd = ReportOotd.of(report, ootd, reporter);

        reportOotdRepository.save(reportOotd);

        Integer ootdReportCount = reportOotdRepository.countByOotd(ootd);
        ootd.updateReportCount(ootdReportCount);

        return ReportResultRes.of(ootd.getId(), ootdReportCount);
    }

}
