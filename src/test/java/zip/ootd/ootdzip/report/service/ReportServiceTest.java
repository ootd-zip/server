package zip.ootd.ootdzip.report.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.controller.response.ReportRes;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportOotdSvcReq;
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

        ReportOotdSvcReq request = ReportOotdSvcReq.of(report.getId(), savedOotd.getId());

        // when & then
        ReportResultRes result = reportService.reportOotd(request, reportUser);

        //then
        assertThat(result)
                .extracting("id", "reportCount")
                .contains(savedOotd.getId(), 1);
    }

    @DisplayName("유효하지 않은 ootdId를 신고하면 에러가 발생한다.")
    @Test
    void reportOotdWithInvalidOotdId() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Report report = createReportBy("신고항목1");

        ReportOotdSvcReq request = ReportOotdSvcReq.of(report.getId(), 0L);

        // when & then
        assertThatThrownBy(() -> reportService.reportOotd(request, reportUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "O005", "유효하지 않은 ootd ID");

    }

    @DisplayName("유효하지 않은 reportId를 신고하면 에러가 발생한다.")
    @Test
    void reportOotdWithInvalidReportId() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Ootd ootd = Ootd.builder()
                .writer(writer)
                .isPrivate(false)
                .contents("내용1")
                .build();

        Ootd savedOotd = ootdRepository.save(ootd);

        ReportOotdSvcReq request = ReportOotdSvcReq.of(0L, savedOotd.getId());

        // when & then
        assertThatThrownBy(() -> reportService.reportOotd(request, reportUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "R001", "유효하지 않은 신고 ID");
    }

    @DisplayName("같은 사람이 같은 ootd를 신고하면 에러가 발생한다.")
    @Test
    void reportOotdWithDuplicateUserAndOotd() {
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

        ReportOotdSvcReq request = ReportOotdSvcReq.of(report.getId(), savedOotd.getId());

        ReportResultRes result = reportService.reportOotd(request, reportUser);

        // when & then
        assertThatThrownBy(() -> reportService.reportOotd(request, reportUser))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "R002", "신고는 한번만 가능합니다.");
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
