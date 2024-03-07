package zip.ootd.ootdzip.report.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.report.service.request.ReportType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ReportServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("모든 신고 항목을 조회한다.")
    @Test
    void getAllReports() {
        // given
        Report report1 = createReportBy("신고항목1");
        Report report2 = createReportBy("신고항목2");

        // when
        List<ReportRes> result = reportService.getAllReports();

        //then
        assertThat(result).hasSize(2)
                .extracting("message")
                .containsExactlyInAnyOrder("신고항목1", "신고항목2");

    }

    @DisplayName("ootd를 신고하면 신고한 ootd Id와 해당 ootd의 신고수를 반환한다.")
    @Test
    void reportOotd() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Ootd ootd = Ootd.builder()
                .writer(writer)
                .isPrivate(false)
                .contents("내용1")
                .build();

        Ootd savedOotd = ootdRepository.save(ootd);

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), savedOotd.getId(), ReportType.OOTD);

        // when & then
        ReportResultRes result = reportService.report(request, reportUser);

        //then
        assertThat(result)
                .extracting("id", "reportCount")
                .contains(savedOotd.getId(), 1);

        Ootd reportedOotd = ootdRepository.findById(savedOotd.getId()).get();

        assertThat(reportedOotd.getReportCount())
                .isEqualTo(result.getReportCount());

    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

    private Report createReportBy(String message) {
        Report report = new Report(message);
        return reportRepository.save(report);
    }
}
