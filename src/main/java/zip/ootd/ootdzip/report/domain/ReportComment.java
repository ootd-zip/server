package zip.ootd.ootdzip.report.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.comment.domain.Comment;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "report_comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportComment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commnet_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
