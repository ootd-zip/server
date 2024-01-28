package zip.ootd.ootdzip.report.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportReq;
import zip.ootd.ootdzip.user.domain.User;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;

    public List<ReportRes> getAllReports() {

        List<Report> allReports = reportRepository.findAll();

        return allReports.stream()
                .map(ReportRes::of)
                .toList();
    }

    public void reportOotd(ReportReq request, User reportUser) {

    }

}
