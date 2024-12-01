package zip.ootd.ootdzip.fcm.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import jakarta.persistence.CascadeType;
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
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.user.domain.User;

@Entity
@Table(name = "fcm_infos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmInfo extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isPermission = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isLogin = true;

    @Column(nullable = false)
    private String fcmToken;

    @Builder.Default
    @OneToMany(mappedBy = "fcmInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<FcmNotificationType> fcmNotificationTypes = new ArrayList<>();

    // fcm 기본생성자
    // 모든 알람에 대해 허용으로 기본으로 생성합니다.
    public static FcmInfo createDefaultFcmInfo(User user, String fcmToken) {

        List<FcmNotificationType> fcmDefaultNotificationTypes = Stream.of(NotificationType.values())
                .map(notificationType -> FcmNotificationType.builder()
                .notificationType(notificationType).build()).toList();

        FcmInfo fcmInfo = FcmInfo.builder()
                .user(user)
                .fcmToken(fcmToken)
                .build();

        fcmInfo.addFcmNotificationTypes(fcmDefaultNotificationTypes);
        return fcmInfo;
    }

    // 해당 기기 사용자가 로그인을 했을 경우
    public void login() {
        this.isLogin = true;
    }

    // 해당 기기 사용자가 로그아웃을 했을 경우
    public void logout() {
        this.isLogin = false;
    }

    public boolean isExistAllowNotificationType(Notification notification) {
        return fcmNotificationTypes.stream()
                .anyMatch(fnt -> fnt.getNotificationType() == notification.getNotificationType() && fnt.getIsAllow());
    }

    // == 연관관계 메서드 == //
    public void addFcmNotificationType(FcmNotificationType fcmNotificationType) {
        fcmNotificationTypes.add(fcmNotificationType);
        fcmNotificationType.setFcmInfo(this);
    }

    public void addFcmNotificationTypes(List<FcmNotificationType> fcmNotificationTypes) {
        fcmNotificationTypes.forEach(this::addFcmNotificationType);
    }
}
