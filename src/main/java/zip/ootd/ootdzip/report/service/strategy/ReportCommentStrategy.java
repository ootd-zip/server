package zip.ootd.ootdzip.report.service.strategy;

import static zip.ootd.ootdzip.common.exception.code.ErrorCode.*;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.comment.repository.CommentRepository;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.report.controller.response.ReportResultRes;
import zip.ootd.ootdzip.report.domain.Report;
import zip.ootd.ootdzip.report.domain.ReportComment;
import zip.ootd.ootdzip.report.repository.ReportCommentRepository;
import zip.ootd.ootdzip.report.repository.ReportRepository;
import zip.ootd.ootdzip.report.service.request.ReportSvcReq;
import zip.ootd.ootdzip.user.domain.User;

@RequiredArgsConstructor
@Component
public class ReportCommentStrategy implements ReportStrategy {

    private final ReportRepository reportRepository;
    private final CommentRepository commentRepository;
    private final ReportCommentRepository reportCommentRepository;

    @Override
    public ReportResultRes report(User reporter, ReportSvcReq request) {

        List<Report> reports = validateReportId(request.getReportIds(), reportRepository);

        Comment comment = commentRepository.findById(request.getTargetId())
                .orElseThrow(() -> new CustomException(NOT_FOUNT_COMMENT_ID));

        checkReporterAndWriter(reporter, comment.getWriter());

        if (reportCommentRepository.existsByCommentAndReporter(comment, reporter)) {
            throw new CustomException(ErrorCode.NOT_DUPLICATE_REPORT);
        }

        List<ReportComment> reportComments = reports.stream()
                .map((report) -> ReportComment.of(report, comment, reporter))
                .toList();
        reportCommentRepository.saveAll(reportComments);

        comment.increaseReportCount();

        return ReportResultRes.of(comment.getId(), comment.getReportCount());
    }
}
