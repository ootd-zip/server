package zip.ootd.ootdzip.notification.repository;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class NotificationRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("읽지않은 알람을 정상적으로 가져옵니다.")
    @Transactional
    @Test
    void getNotifications() {
        // given
        User user = createUserBy("알람수신자");
        User user1 = createUserBy("알람받는자");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.TAG_COMMENT, false);
        Notification noti2 = createNotification(user, user1, NotificationType.LIKE, false);
        Notification noti3 = createNotification(user, user1, NotificationType.FOLLOW, false);
        Notification noti4 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti5 = createNotification(user, user1, NotificationType.TAG_COMMENT, true);
        Notification noti6 = createNotification(user, user1, NotificationType.LIKE, true);
        Notification noti7 = createNotification(user, user1, NotificationType.FOLLOW, true);
        Notification noti8 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        int page = 0;
        int size = 10;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // when
        Slice<Notification> results = notificationRepository.findByUserIdAndIsRead(user.getId(), false,
                pageable);

        // then
        assertThat(results.getContent())
                .hasSize(4)
                .extracting("id")
                .containsExactly(noti3.getId(), noti2.getId(), noti1.getId(), noti.getId());
    }

    @DisplayName("읽은 알람을 정상적으로 가져옵니다.")
    @Transactional
    @Test
    void getReadNotifications() {
        // given
        User user = createUserBy("알람수신자");
        User user1 = createUserBy("알람받는자");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.TAG_COMMENT, false);
        Notification noti2 = createNotification(user, user1, NotificationType.LIKE, false);
        Notification noti3 = createNotification(user, user1, NotificationType.FOLLOW, false);
        Notification noti4 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti5 = createNotification(user, user1, NotificationType.TAG_COMMENT, true);
        Notification noti6 = createNotification(user, user1, NotificationType.LIKE, true);
        Notification noti7 = createNotification(user, user1, NotificationType.FOLLOW, true);
        Notification noti8 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        int page = 0;
        int size = 30;
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // when
        Slice<Notification> results = notificationRepository.findByUserIdAndIsRead(user.getId(), true,
                pageable);

        // then
        assertThat(results.getContent())
                .hasSize(4)
                .extracting("id")
                .containsExactly(noti7.getId(), noti6.getId(), noti5.getId(), noti4.getId());
    }

    @DisplayName("유저와 읽음처리에 해당하는 알림의 개수를 반환합니다.")
    @Transactional
    @Test
    void findCountByUserIdAndIsRead() {
        // given
        User user = createUserBy("알람수신자");
        User user1 = createUserBy("알람받는자");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.TAG_COMMENT, false);
        Notification noti2 = createNotification(user, user1, NotificationType.LIKE, false);
        Notification noti3 = createNotification(user, user1, NotificationType.FOLLOW, false);
        Notification noti4 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti5 = createNotification(user, user1, NotificationType.TAG_COMMENT, true);
        Notification noti6 = createNotification(user, user1, NotificationType.LIKE, true);
        Notification noti7 = createNotification(user, user1, NotificationType.FOLLOW, true);
        Notification noti8 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        // when
        Long result = notificationRepository.findCountByUserIdAndIsRead(user.getId(), true);

        // then
        assertThat(result).isEqualTo(4L);
    }

    private Notification createNotification(User receiver, User sender, NotificationType notificationType,
            Boolean isRead) {

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(notificationType)
                .isRead(isRead)
                .goUrl("")
                .content("")
                .imageUrl("")
                .isPush(false)
                .build();

        return notificationRepository.save(notification);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
