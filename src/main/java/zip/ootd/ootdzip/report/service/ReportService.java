package zip.ootd.ootdzip.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.lock.annotation.RLockCustom;
import zip.ootd.ootdzip.lock.domain.RLockType;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.report.service.strategy.ReportStrategy;
import zip.ootd.ootdzip.report.service.strategy.ReportStrategyProvider;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final ReportStrategyProvider reportStrategyProvider;

    @Transactional(readOnly = true)
    public List<ReportRes> getAllReports() {

        List<Report> allReports = reportRepository.findAll();

        return allReports.stream()
                .map(ReportRes::of)
                .toList();
    }

    @RLockCustom(type = RLockType.REPORT_COUNT, keys = {"#request.getReportType()", "#request.getTargetId()"})
    @Transactional
    public ReportResultRes report(ReportSvcReq request, User reporter) {
        final ReportStrategy reportStrategy = reportStrategyProvider.getStrategy(request.getReportType());
        return reportStrategy.report(reporter, request);
    }
}
