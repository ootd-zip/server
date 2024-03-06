package zip.ootd.ootdzip.notification.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.notification.service.NotificationService;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    /**
     * TransactionalEventListener 은 기본 옵션으로 이전 수행중인 트랜잭션 커밋 완료 후에 수행됩니다.(롤백이 됐는데 알람이 가면 안되므로)
     * 동기로 처리하면 Transactional(propagation = Propagation.REQUIRES_NEW) 가 필요합니다.
     * 왜냐하면 커밋 완료 후 수행이므로, 트랜잭션이 종료됐기 때문에 추가로 새로운 트랜잭션을 열 필요가 있습니다.
     * 해당 로직은 비동기로 처리했습니다. 비동기로 처리시 다른 스레드에서 트랜잭션을 생성하므로
     * Transactional(propagation = Propagation.REQUIRES_NEW) 는 필요 없습니다.
     * 알림 로직은 즉시 처리 될 필요없고 원래 로직은 해당 알림 로직과 독립적으로 처리하기 위해서 비동기를 사용했습니다.
     */
    @TransactionalEventListener
    @Async
    public void handleNotification(NotificationEvent notificationEvent) {
        notificationService.saveNotification(notificationEvent.getReceiver(),
                notificationEvent.getSender(),
                notificationEvent.getNotificationType(),
                notificationEvent.getContent(),
                notificationEvent.getImageUrl(),
                notificationEvent.getGoUrl());
    }
}
