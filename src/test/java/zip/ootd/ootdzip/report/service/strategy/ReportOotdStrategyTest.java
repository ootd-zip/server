package zip.ootd.ootdzip.report.service.strategy;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.report.service.request.ReportType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

@Transactional
class ReportOotdStrategyTest extends IntegrationTestSupport {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportOotdStrategy reportOotdStrategy;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("ootd를 신고하면 신고한 ootd Id와 해당 ootd의 신고수를 반환한다.")
    @Test
    void reportOotd() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Ootd savedOotd = createOotdBy(writer);

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), savedOotd.getId(), ReportType.OOTD);

        // when & then
        ReportResultRes result = reportOotdStrategy.report(reportUser, request);

        //then
        assertThat(result)
                .extracting("id", "reportCount")
                .contains(savedOotd.getId(), 1);

        Ootd reportedOotd = ootdRepository.findById(savedOotd.getId()).get();

        assertThat(reportedOotd.getReportCount())
                .isEqualTo(result.getReportCount());

    }

    @DisplayName("유효하지 않은 ootdId를 신고하면 에러가 발생한다.")
    @Test
    void reportOotdWithInvalidOotdId() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), 0L, ReportType.OOTD);

        // when & then
        assertThatThrownBy(() -> reportOotdStrategy.report(reportUser, request))
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

        Ootd savedOotd = createOotdBy(writer);

        ReportSvcReq request = ReportSvcReq.of(List.of(0L), savedOotd.getId(), ReportType.OOTD);

        // when & then
        assertThatThrownBy(() -> reportOotdStrategy.report(reportUser, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "R001", "유효하지 않은 신고 ID");
    }

    @DisplayName("같은 사람이 같은 ootd를 2번 이상 신고하면 에러가 발생한다.")
    @Test
    void reportOotdWithDuplicateUserAndOotd() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser = createUserBy("신고자1");

        Ootd savedOotd = createOotdBy(writer);

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), savedOotd.getId(), ReportType.OOTD);

        ReportResultRes result = reportOotdStrategy.report(reportUser, request);

        // when & then
        assertThatThrownBy(() -> reportOotdStrategy.report(reportUser, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(200, "R002", "신고는 한번만 가능합니다.");
    }

    @DisplayName("자신이 작성한 ootd를 신고하면 에러가 발생한다.")
    @Test
    void reportMyOotd() {
        // given
        User writer = createUserBy("작성자1");

        Ootd savedOotd = createOotdBy(writer);

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), savedOotd.getId(), ReportType.OOTD);

        // when & then
        assertThatThrownBy(() -> reportOotdStrategy.report(writer, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "R003", "작성자는 신고가 불가능합니다.");
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

    private Ootd createOotdBy(User writer) {
        Ootd ootd = Ootd.builder()
                .writer(writer)
                .isPrivate(false)
                .contents("내용1")
                .build();

        return ootdRepository.save(ootd);
    }
}
