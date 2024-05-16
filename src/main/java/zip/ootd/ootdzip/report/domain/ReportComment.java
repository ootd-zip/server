package zip.ootd.ootdzip.report.domain;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "report_comments")
@Getter
@NoArgsConstructor
public class ReportComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commnet_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User reporter;

    @Builder
    private ReportComment(Report report, Comment comment, User reporter) {
        this.report = report;
        this.comment = comment;
        this.reporter = reporter;
    }

    public static ReportComment of(Report report, Comment comment, User reporter) {
        return ReportComment.builder()
                .report(report)
                .comment(comment)
                .reporter(reporter)
                .build();
    }

}
