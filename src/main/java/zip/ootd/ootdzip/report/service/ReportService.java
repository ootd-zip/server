package zip.ootd.ootdzip.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportOotd;
import zip.ootd.ootdzip.report.repository.ReportOotdRepository;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportOotdSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final OotdRepository ootdRepository;
    private final ReportOotdRepository reportOotdRepository;

    public List<ReportRes> getAllReports() {

        List<Report> allReports = reportRepository.findAll();

        return allReports.stream()
                .map(ReportRes::of)
                .toList();
    }

    public ReportResultRes reportOotd(ReportOotdSvcReq request, User reportUser) {

        Report report = reportRepository.findById(request.getReportId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_REPORT_ID));

        Ootd ootd = ootdRepository.findById(request.getOotdId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_OOTD_ID));

        ReportOotd reportOotd = ReportOotd.of(report, ootd, reportUser);

        if (reportOotdRepository.existsByOotdAndUser(ootd, reportUser)) {
            throw new CustomException(ErrorCode.NOT_DUPLICATE_REPORT);
        }

        ReportOotd savedReportOotd = reportOotdRepository.save(reportOotd);

        Integer countByOotd = reportOotdRepository.countByOotd(savedReportOotd.getOotd());

        return ReportResultRes.of(savedReportOotd.getOotd().getId(), countByOotd);
    }

}
