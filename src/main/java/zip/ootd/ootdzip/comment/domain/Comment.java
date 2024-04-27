package zip.ootd.ootdzip.comment.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.user.domain.User;

/**
 * 댓글 조회시 기본 필터링으로 가져오는 댓글
 * 1. 부모댓글, 삭제X, 신고수 5 미만, 자식댓글없음
 * 2. 부모댓글, 자식댓글존재
 * 3. 자식댓글, 삭제X, 신고수 5 미만
 */
@Entity
@Table(name = "comments")
@Where(clause = "(depth = 1 AND is_deleted = false AND report_count < 5 AND child_count = 0) "
        + "OR (depth = 1 AND child_count > 0) " + "OR (depth = 2 AND is_deleted = false AND report_count < 5)")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ootd_id")
    private Ootd ootd;

    // 댓글의 경우 탈퇴된 유저의 부모 댓글은 "삭제된 댓글입니다." 로 표시해야하므로 db 에서 지울 수 없어, writer 가 nullable 해야한다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User writer;

    @ManyToOne(fetch = FetchType.LAZY)
    private User taggedUser;

    @Builder.Default
    private int depth = 0;

    @Builder.Default
    private int reportCount = 0;

    @Builder.Default
    private int childCount = 0;

    private String contents;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isDeleted = false;

    private LocalDateTime deletedAt;

    @Builder.Default
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent = null;

    @Builder.Default
    @OneToMany(mappedBy = "parent")
    private List<Comment> childComments = new ArrayList<>();

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private Long groupOrder;

    public void addChildComment(Comment comment) {
        childComments.add(comment);
        comment.setParent(this);

        this.childCount++;
    }

    public void increaseReportCount() {
        this.reportCount += 1;
    }

    public void deleteComment() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();

        if (this.parent != null) {
            parent.setChildCount(parent.getChildCount() - 1);
        }
    }

    // == Custom Getter == //

    /**
     * 댓글 (삭제 또는 신고 수 일정 이상), 대댓글 있을시 "삭제된 댓글입니다" 로 표시
     * 댓글 (삭제 또는 신고 수 일정 이상), 대댓글 없을시 댓글 삭제
     * 대댓글 (삭제 또는 신고 수 일정 이상) 시 대댓글 삭제
     */
    public String getContents() {

        if (writer == null || writer.getIsDeleted() || isDeleted || reportCount >= 5) {
            return "삭제된 댓글입니다.";
        }

        return contents;
    }

    public String getTaggedUserName() {
        if (taggedUser == null) {
            return "";
        }

        return taggedUser.getName();
    }

    public Long getParentCommentId() {
        if (parent == null) {
            return null;
        }

        return parent.getId();
    }
}
