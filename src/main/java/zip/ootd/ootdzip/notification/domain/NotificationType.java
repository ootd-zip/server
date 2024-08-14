package zip.ootd.ootdzip.notification.domain;

import lombok.Getter;

@Getter
public enum NotificationType {
    FOLLOW("님이 회원님을 팔로우합니다."),
    OOTD_COMMENT("님이 회원님의 ootd에 댓글을 남겼습니다."),
    TAG_COMMENT("님이 회원님의 댓글에 답글을 남겼습니다."),
    LIKE("님이 회원님의 ootd를 좋아합니다."),
    BRAND_REQUEST_APPROVED("님이 건의한 브랜드가 추가되었습니다.");

    private final String baseMessage;

    NotificationType(String baseMessage) {
        this.baseMessage = baseMessage;
    }
}
