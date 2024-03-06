package zip.ootd.ootdzip.notification.domain;

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
}
