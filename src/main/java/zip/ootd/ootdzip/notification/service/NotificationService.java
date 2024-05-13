package zip.ootd.ootdzip.notification.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.response.CommonSliceResponse;
import zip.ootd.ootdzip.notification.data.NotificationGetAllReq;
import zip.ootd.ootdzip.notification.data.NotificationGetAllRes;
import zip.ootd.ootdzip.notification.domain.Notification;
import zip.ootd.ootdzip.notification.domain.NotificationType;
import zip.ootd.ootdzip.notification.repository.EmitterRepository;
import zip.ootd.ootdzip.notification.repository.NotificationRepository;
import zip.ootd.ootdzip.user.domain.User;
import zip.ootd.ootdzip.user.service.UserService;
import zip.ootd.ootdzip.userblock.repository.UserBlockRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final UserBlockRepository userBlockRepository;

    public SseEmitter subscribe(User loginUser, String lastEventId) {
        Long userId = loginUser.getId();
        String emitterId = makeEmitterIdByUserId(userId);
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        // SSE 연결후 데이터를 하나도 안보내면 503 응답이 발생함
        String eventId = makeEmitterIdByUserId(userId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [UserId =" + userId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            sendLostData(lastEventId, userId, emitterId, emitter);
        }

        return emitter;
    }

    private String makeEmitterIdByUserId(Long userId) {
        return userId + "_" + System.currentTimeMillis();
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("알람을 위한 클라이언트와 연결중에 오류가 발생했습니다.");
        }
    }

    private void sendLostData(String lastEventId, Long userId, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByUserId(String.valueOf(userId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    public Notification saveNotification(User receiver,
            User sender,
            NotificationType notificationType,
            String content,
            String imageUrl,
            String goUrl) {

        return notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .notificationType(notificationType)
                .content(content)
                .imageUrl(imageUrl)
                .goUrl(goUrl)
                .build());
    }

    //TODO: 푸쉬 알림 필요시 스펙에 맞게 개발,
    // 알람종류별로 보낼지말지 필터링하는 것도 필요
    // 해당 기능 사용시 AOP 에서 중복 알람저장안되도록 saveNotification 은 지워주기
    public void send(User receiver, User sender,
            NotificationType notificationType, String content, String imageUrl, String goUrl) {

        Notification notification = saveNotification(receiver, sender, notificationType, content, imageUrl, goUrl);

        Long userId = receiver.getId();
        String eventId = userId + "_" + System.currentTimeMillis();
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByUserId(String.valueOf(userId));
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    sendNotification(emitter, eventId, key, new Notification());
                }
        );
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
