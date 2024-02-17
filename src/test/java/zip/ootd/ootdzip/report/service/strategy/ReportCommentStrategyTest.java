package zip.ootd.ootdzip.report.service.strategy;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
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

class ReportCommentStrategyTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportCommentStrategy reportCommentStrategy;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @DisplayName("댓글을 신고하면 신고한 댓글 Id와 해당 댓글의 신고수를 반환한다.")
    @Test
    void reportComment() {
        // given
        Report report = createReportBy("신고항목1");

        User writer = createUserBy("작성자1");
        User reporter = createUserBy("신고자1");

        Ootd ootd = createOotdBy(writer);

        Comment comment = createCommentBy(writer, ootd);

        ReportSvcReq request = ReportSvcReq.of(report.getId(), comment.getId(), ReportType.COMMENT);

        // when
        ReportResultRes result = reportCommentStrategy.report(reporter, request);

        //then
        assertThat(result)
                .extracting("id", "reportCount")
                .contains(comment.getId(), 1);

        Comment reportedComment = commentRepository.findById(comment.getId()).get();

        assertThat(reportedComment.getReportCount())
                .isEqualTo(result.getReportCount());
    }

    @DisplayName("유효하지 않은 댓글 ID를 신고하면 에러가 발생한다.")
    @Test
    void reportCommentWithInvalidCommentId() {
        // given
        Report report = createReportBy("신고항목1");

        User writer = createUserBy("작성자1");
        User reporter = createUserBy("신고자1");

        ReportSvcReq request = ReportSvcReq.of(report.getId(), 1L, ReportType.COMMENT);

        //when & then
        assertThatThrownBy(() -> reportCommentStrategy.report(reporter, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "CM001", "유효하지 않은 댓글 ID");
    }

    @DisplayName("유효하지 않은 reportID를 신고하면 에러가 발생한다.")
    @Test
    void reportCommentWithInvalidReportId() {
        // given
        User writer = createUserBy("작성자1");
        User reporter = createUserBy("신고자1");

        Ootd ootd = createOotdBy(writer);

        Comment comment = createCommentBy(writer, ootd);

        ReportSvcReq request = ReportSvcReq.of(1L, comment.getId(), ReportType.COMMENT);

        //when & then
        assertThatThrownBy(() -> reportCommentStrategy.report(reporter, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(404, "R001", "유효하지 않은 신고 ID");
    }

    @DisplayName("같은 사람이 같은 댓글을 2번 이상 신고하면 에러가 발생한다.")
    @Test
    void reportCommentWithDuplicateUserAndComment() {
        //given
        Report report = createReportBy("신고항목1");

        User writer = createUserBy("작성자1");
        User reporter = createUserBy("신고자1");

        Ootd ootd = createOotdBy(writer);

        Comment comment = createCommentBy(writer, ootd);

        ReportSvcReq request = ReportSvcReq.of(report.getId(), comment.getId(), ReportType.COMMENT);

        ReportResultRes result = reportCommentStrategy.report(reporter, request);

        //when & then
        assertThatThrownBy(() -> reportCommentStrategy.report(reporter, request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode.status", "errorCode.divisionCode", "errorCode.message")
                .contains(400, "R002", "신고는 한번만 가능합니다.");
    }

    @DisplayName("작성자가 신고하면 에러가 발생한다.")
    @Test
    void reportMyComment() {
        // given
        Report report = createReportBy("신고항목1");

        User writer = createUserBy("작성자1");

        Ootd ootd = createOotdBy(writer);

        Comment comment = createCommentBy(writer, ootd);

        ReportSvcReq request = ReportSvcReq.of(report.getId(), comment.getId(), ReportType.COMMENT);

        // when&then
        assertThatThrownBy(() -> reportCommentStrategy.report(writer, request))
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

    private Comment createCommentBy(User writer, Ootd ootd) {
        Comment comment = Comment.builder()
                .topOotdId(ootd.getId())
                .depth(1)
                .writer(writer)
                .contents("내용1")
                .build();

        return commentRepository.save(comment);
    }

}
