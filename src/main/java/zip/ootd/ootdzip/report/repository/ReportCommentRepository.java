package zip.ootd.ootdzip.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.report.domain.ReportComment;
import zip.ootd.ootdzip.user.domain.User;

@Repository
public interface ReportCommentRepository extends JpaRepository<ReportComment, Long> {

    boolean existsByCommentAndReporter(Comment comment, User reporter);
}
