package zip.ootd.ootdzip.comment.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        + "OR (depth = 1 AND child_count > 0) "
        + "OR (depth = 2 AND is_deleted = false AND report_count < 5)")
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

    @Column(nullable = false)
    private Long topOotdId;

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

    public void addChildComment(Comment comment) {
        childComments.add(comment);
        comment.setParent(this);

        this.childCount++;
    }

    /**
     * 1분 미만 : 지금
     * 60분 미만 : 몇분전
     * 24시간 미만 : 몇시간전
     * 일주일 미만 : 몇일전
     * 한달 미만 : 몇주전
     * 일년 미만 : 몇개월전
     * 일년 이상 : 몇년전
     */
    public String compareCreatedTimeAndNow() {
        LocalDateTime createdTimeLT = this.createdAt;
        LocalDateTime nowLT = LocalDateTime.now();

        LocalDate createdTimeLD = this.createdAt.toLocalDate();
        LocalDate nowLD = LocalDateTime.now().toLocalDate();

        long seconds = ChronoUnit.SECONDS.between(createdTimeLT, nowLT);
        if (seconds < 60) {
            return "지금";
        } else if (seconds < 3600) { // 3600 = 1시간
            long minutes = ChronoUnit.MINUTES.between(createdTimeLT, nowLT);
            return minutes + "분전";
        } else if (seconds < 86400) { //86400 = 1일
            long hours = ChronoUnit.HOURS.between(createdTimeLT, nowLT);
            return hours + "시간전";
        } else if (seconds < 604800) { //604800 = 1주일
            long days = ChronoUnit.DAYS.between(createdTimeLD, nowLD);
            return days + "일전";
        } else if (seconds < 3024000 && ChronoUnit.MONTHS.between(createdTimeLD, nowLD) == 0) { //3024000 = 35일
            long weeks = ChronoUnit.WEEKS.between(createdTimeLD, nowLD);
            return weeks + "주전";
        } else if (seconds < 31536000 && ChronoUnit.YEARS.between(createdTimeLD, nowLD) == 0) {
            // 31536000 = 365일, 1년이 366일때는 대비해 년도 비교 추가
            long months = ChronoUnit.MONTHS.between(createdTimeLD, nowLD);
            return months + "달전";
        } else {
            long years = ChronoUnit.YEARS.between(createdTimeLD, nowLD);
            return years + "년전";
        }
    }

    public void increaseReportCount() {
        this.reportCount += 1;
    }

    /**
     * 댓글 (삭제 또는 신고 수 일정 이상), 대댓글 있을시 "삭제된 댓글입니다" 로 표시
     * 댓글 (삭제 또는 신고 수 일정 이상), 대댓글 없을시 댓글 삭제
     * 대댓글 (삭제 또는 신고 수 일정 이상) 시 대댓글 삭제
     */
    public String getContents() {

        if (isDeleted || reportCount >= 5) {
            return "삭제된 댓글입니다.";
        }

        return contents;
    }

    public void deleteComment() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();

        if (this.parent != null) {
            parent.childCount--;
        }
    }
}
