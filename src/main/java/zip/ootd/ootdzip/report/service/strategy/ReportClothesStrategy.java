package zip.ootd.ootdzip.report.service.strategy;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.clothes.domain.Clothes;
import zip.ootd.ootdzip.clothes.repository.ClothesRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportClothes;
import zip.ootd.ootdzip.report.repository.ReportClothesRepository;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@RequiredArgsConstructor
@Component
public class ReportClothesStrategy implements ReportStrategy {

    private final ReportRepository reportRepository;
    private final ClothesRepository clothesRepository;
    private final ReportClothesRepository reportClothesRepository;

    @Override
    public ReportResultRes report(User reporter, ReportSvcReq request) {

        List<Report> reports = validateReportId(request.getReportIds(), reportRepository);

        Clothes clothes = clothesRepository.findById(request.getTargetId())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CLOTHES_ID));

        checkReporterAndWriter(reporter, clothes.getUser());

        if (reportClothesRepository.existsByClothesAndReporter(clothes, reporter)) {
            throw new CustomException(ErrorCode.NOT_DUPLICATE_REPORT);
        }

        List<ReportClothes> reportClothes = reports.stream()
                .map((report) -> ReportClothes.of(report, clothes, reporter))
                .toList();
        reportClothesRepository.saveAll(reportClothes);

        clothes.increaseReportCount();

        return ReportResultRes.of(clothes.getId(), clothes.getReportCount());
    }
}
