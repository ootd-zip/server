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
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.repository.ReportRepository;
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
        Report report1 = new Report("신고항목1");
        Report report2 = new Report("신고항목2");

        reportRepository.saveAll(List.of(report1, report2));

        // when
        List<ReportRes> result = reportService.getAllReports();

        //then
        assertThat(result).hasSize(2)
                .extracting("message")
                .containsExactlyInAnyOrder("신고항목1", "신고항목2");

    }

    @DisplayName("ootd를 신고한다.")
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

        // when
        reportService.reportOotd();

        //then
    }

    @DisplayName("같은 사람이 같은 ootd를 신고하면 실패한다.")
    @Test
    void reportOotdWithDuplicateUserAndOotd() {
        // given

        // when

        //then
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }

}
