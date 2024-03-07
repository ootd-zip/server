package zip.ootd.ootdzip.notification.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "notifications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isRead = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPush = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    // 해당 content 는 현재 댓글 알람에서만 사용됩니다.
    private String content;

    // 해당 imageUrl 은 현재 ootd 좋아요, 댓글 알림에 사용됩니다.
    private String imageUrl;

    // 푸쉬 알람 사용시 이동할 url 링크
    private String goUrl;

    public String compareSimpleCreatedTimeAndNow() {
        LocalDateTime createdTimeLT = this.createdAt;
        LocalDateTime nowLT = LocalDateTime.now();

        LocalDate createdTimeLD = this.createdAt.toLocalDate();
        LocalDate nowLD = LocalDateTime.now().toLocalDate();

        long seconds = ChronoUnit.SECONDS.between(createdTimeLT, nowLT);

        if (seconds < 86400) { //86400 = 1일
            return "오늘";
        } else if (seconds < 86400 * 2) { // 2일
            return "어제";
        } else if (seconds < 86400 * 7) { // 7일
            return "최근 일주일";
        } else if (seconds < 86400 * 31 && ChronoUnit.MONTHS.between(createdTimeLD, nowLD) == 0) { // 한달
            return "최근 한 달";
        } else {
            return "오래 전";
        }
    }

    public void readNotification() {
        this.isRead = true;
    }
}
