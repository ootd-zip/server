package zip.ootd.ootdzip.notification.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import org.springframework.transaction.annotation.Transactional;

import zip.ootd.ootdzip.IntegrationTestSupport;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.notification.data.NotificationGetAllReq;
import zip.ootd.ootdzip.notification.data.NotificationGetAllRes;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.repository.NotificationRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.repository.UserRepository;

public class NotificationServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @DisplayName("알람을 정상적으로 저장합니다.")
    @Test
    void saveNotification() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");

        // when
        Notification result = notificationService.saveNotification(user1, user, NotificationType.OOTD_COMMENT,
                "hi", "oh", "ho");

        // then
        Notification savedResult = notificationRepository.findById(result.getId()).get();
        assertThat(savedResult).extracting("id", "receiver.id", "sender.id",
                        "content", "imageUrl", "goUrl", "notificationType")
                .contains(result.getId(), user1.getId(), user.getId(), "hi", "oh", "ho", NotificationType.OOTD_COMMENT);
    }

    @DisplayName("읽지않은 알람을 정상적으로 가져옵니다.")
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

        NotificationGetAllReq notificationGetAllReq = new NotificationGetAllReq();
        notificationGetAllReq.setIsRead(false);
        notificationGetAllReq.setPage(0);
        notificationGetAllReq.setSize(10);
        notificationGetAllReq.setSortCriteria("createdAt");
        notificationGetAllReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<NotificationGetAllRes> results = notificationService.getNotifications(user,
                notificationGetAllReq);

        // then
        assertThat(results.getContent())
                .hasSize(4)
                .extracting("id")
                .containsExactly(noti3.getId(), noti2.getId(), noti1.getId(), noti.getId());
    }

    @DisplayName("읽은 알람을 정상적으로 가져옵니다.")
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

        NotificationGetAllReq notificationGetAllReq = new NotificationGetAllReq();
        notificationGetAllReq.setIsRead(true);
        notificationGetAllReq.setPage(0);
        notificationGetAllReq.setSize(30);
        notificationGetAllReq.setSortCriteria("createdAt");
        notificationGetAllReq.setSortDirection(Sort.Direction.DESC);

        // when
        CommonSliceResponse<NotificationGetAllRes> results = notificationService.getNotifications(user,
                notificationGetAllReq);

        // then
        assertThat(results.getContent())
                .hasSize(4)
                .extracting("id")
                .containsExactly(noti7.getId(), noti6.getId(), noti5.getId(), noti4.getId());
    }

    @DisplayName("알람을 정상적으로 읽음으로 수정 합니다.")
    @Test
    void updateIsRead() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti2 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        // when
        notificationService.updateIsRead(user, noti.getId());

        // then
        Notification result = notificationRepository.findById(noti.getId()).get();
        assertThat(result.getIsRead()).isEqualTo(true);
    }

    @DisplayName("알람을 정상적으로 읽음/읽지 않음으로 수정시 다른사람이 수정시 예외가 발생합니다.")
    @Test
    void updateIsReadByOtherUser() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti2 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        // when & then
        assertThatThrownBy(() -> notificationService.updateIsRead(user1, noti.getId()))
                .isInstanceOf(IllegalArgumentException.class);
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

    @DisplayName("읽지 않은 알람이 있을시 true, 아니면 false 를 반환합니다.")
    @Test
    void getIsReadExist() {
        // given
        User user = createUserBy("유저");
        User user1 = createUserBy("유저1");
        User user2 = createUserBy("유저2");
        Notification noti = createNotification(user, user1, NotificationType.OOTD_COMMENT, false);
        Notification noti1 = createNotification(user, user1, NotificationType.OOTD_COMMENT, true);
        Notification noti2 = createNotification(user1, user, NotificationType.OOTD_COMMENT, true);
        Notification noti3 = createNotification(user1, user, NotificationType.OOTD_COMMENT, true);
        Notification noti4 = createNotification(user1, user, NotificationType.OOTD_COMMENT, false);

        // when
        Boolean result = notificationService.getIsReadExist(user);
        Boolean result1 = notificationService.getIsReadExist(user2);

        // then
        assertThat(result).isEqualTo(true);
        assertThat(result1).isEqualTo(false);
    }

    private User createUserBy(String userName) {
        User user = User.getDefault();
        user.setName(userName);
        return userRepository.save(user);
    }
}
