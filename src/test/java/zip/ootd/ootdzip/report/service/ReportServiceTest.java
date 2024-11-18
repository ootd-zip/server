package zip.ootd.ootdzip.report.service;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.DBCleanUp;
import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.dao.RedisDao;
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

    @Autowired
    private DBCleanUp dbCleanUp;

    @Autowired
    private RedisDao redisDao;

    @AfterEach
    void tearDown() {
        dbCleanUp.execute();
        redisDao.deleteAll();
    }

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

    @DisplayName("신고 수 증가에 대한 동시성이 보장된다.")
    @Test
    public void increaseConcurrencyReportCount() throws InterruptedException {

        // given
        int numberOfThreads = 4;
        ExecutorService service = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        User writer = createUserBy("작성자1");
        List<User> reportUsers = new ArrayList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            reportUsers.add(createUserBy("신고자" + i));
        }

        Ootd ootd = Ootd.builder()
                .writer(writer)
                .isPrivate(false)
                .contents("내용1")
                .build();

        Ootd savedOotd = ootdRepository.save(ootd);

        Report report = createReportBy("신고항목1");

        ReportSvcReq request = ReportSvcReq.of(List.of(report.getId()), savedOotd.getId(), ReportType.OOTD);

        //when
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i;
            service.execute(() -> {
                try {
                    reportService.report(request, reportUsers.get(index));
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // then
        Ootd result = ootdRepository.findById(savedOotd.getId()).orElseThrow();
        assertThat(result.getReportCount()).isEqualTo(4);
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
