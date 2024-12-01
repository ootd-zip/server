package zip.ootd.ootdzip.fcm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import zip.ootd.ootdzip.common.entity.BaseEntity;
import zip.ootd.ootdzip.notification.domain.NotificationType;

@Entity
@Table(name = "fcm_notification_types")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FcmNotificationType extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "fcm_info_id", nullable = false)
    private FcmInfo fcmInfo;

    private NotificationType notificationType;

    @Builder.Default
    private Boolean isAllow = true;

    public void changeAllow(Boolean isAllow) {
        this.isAllow = isAllow;
    }
}
