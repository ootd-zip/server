package zip.ootd.ootdzip.notification.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.fcm.service.FcmService;
import zip.ootd.ootdzip.notification.data.NotificationGetAllReq;
import zip.ootd.ootdzip.notification.data.NotificationGetAllRes;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.repository.NotificationRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;
    private final FcmService fcmService;

    public Notification saveNotification(User receiver,
            User sender,
            NotificationType notificationType,
            String content,
            String imageUrl,
            String goUrl) {

        Notification notification = notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(notificationType)
                .content(content)
                .imageUrl(imageUrl)
                .goUrl(goUrl)
                .build());

        // 푸쉬 알람 보내기
        fcmService.sendMessage(notification);

        return notification;
    }

    public CommonSliceResponse<NotificationGetAllRes> getNotifications(User loginUesr, NotificationGetAllReq request) {

        Pageable pageable = request.toPageable();

        Set<Long> nonAccessibleUserIds = userBlockRepository.getNonAccessibleUserIds(loginUesr.getId());

        Slice<Notification> notifications = notificationRepository.findByUserIdAndIsReadAndSenderIdNotIn(
                loginUesr.getId(),
                request.getIsRead(),
                nonAccessibleUserIds,
                pageable);

        List<NotificationGetAllRes> notificationGetAllResList = notifications.stream()
                .map(NotificationGetAllRes::of)
                .collect(Collectors.toList());

        return new CommonSliceResponse<>(notificationGetAllResList, pageable, notifications.isLast());
    }

    public void updateIsRead(User loginUser, Long id) {

        Notification notification = notificationRepository.findById(id).orElseThrow();
        userService.checkValidUser(loginUser, notification.getReceiver());

        notification.readNotification();
    }

    public Boolean getIsReadExist(User loginUser) {
        Long isReadCount = notificationRepository.countByUserIdAndIsRead(loginUser.getId(), false);
        return isReadCount > 0;
    }
}
