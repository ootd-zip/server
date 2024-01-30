package zip.ootd.ootdzip.report.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportOotd;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ReportOotdRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private ReportOotdRepository reportOotdRepository;

    @DisplayName("사용자가 ootd를 신고한적이 있으면 true를 반환한다.")
    @Test
    void existsByOotdAndUser() {
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

        ReportOotd reportOotd = ReportOotd.of(report, ootd, reportUser);

        reportOotdRepository.save(reportOotd);
        // when
        boolean result = reportOotdRepository.existsByOotdAndReporter(ootd, reportUser);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("사용자가 ootd를 신고한적이 없으면 false를 반환한다.")
    @Test
    void noExistsByOotdAndUser() {
        // given
        User writer = createUserBy("작성자1");
        User reportUser1 = createUserBy("신고자1");
        User reportUser2 = createUserBy("신고자2");

        Ootd ootd = Ootd.builder()
                .writer(writer)
                .isPrivate(false)
                .contents("내용1")
                .build();

        Ootd savedOotd = ootdRepository.save(ootd);

        Report report = createReportBy("신고항목1");

        ReportOotd reportOotd = ReportOotd.of(report, ootd, reportUser1);

        reportOotdRepository.save(reportOotd);
        // when
        boolean result = reportOotdRepository.existsByOotdAndReporter(ootd, reportUser2);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("Ootd가 신고된 회수를 조회한다.")
    @Test
    void countByOotd() {
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

        ReportOotd reportOotd = ReportOotd.of(report, ootd, reportUser);

        reportOotdRepository.save(reportOotd);

        // when
        Integer result = reportOotdRepository.countByOotd(savedOotd);

        //then
        assertThat(result).isEqualTo(1);
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
