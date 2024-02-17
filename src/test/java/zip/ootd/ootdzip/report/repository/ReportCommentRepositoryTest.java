package zip.ootd.ootdzip.report.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportComment;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

class ReportCommentRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private OotdRepository ootdRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ReportCommentRepository reportCommentRepository;

    @DisplayName("신고자가 댓글을 신고한적이 있다면 true를 반환한다")
    @Test
    void existsByCommentAndReporter() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Ootd ootd = createOotdBy(user);
        Comment comment = createCommentBy(user, ootd);

        ReportComment reportComment = ReportComment.of(report, comment, reporter);
        reportCommentRepository.save(reportComment);
        // when
        boolean result = reportCommentRepository.existsByCommentAndReporter(comment, reporter);

        //then
        assertThat(result).isTrue();
    }

    @DisplayName("신고자가 댓글을 신고한적이 있다면 false를 반환한다")
    @Test
    void noExistsByCommentAndReporter() {
        // given
        Report report = createReportBy("신고항목1");

        User user = createUserBy("유저1");
        User reporter = createUserBy("신고자1");

        Ootd ootd = createOotdBy(user);
        Comment comment = createCommentBy(user, ootd);

        // when
        boolean result = reportCommentRepository.existsByCommentAndReporter(comment, reporter);

        //then
        assertThat(result).isFalse();
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
