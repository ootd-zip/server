package zip.ootd.ootdzip.notification.domain;

public enum NotificationType {
    FOLLOW("님이 회원님을 팔로우합니다."),
    OOTD_COMMENT("님이 회원님의 ootd에 댓글을 남겼습니다."),
    TAG_COMMENT("님이 회원님의 댓글에 댓글을 남겼습니다."),
    LIKE("님이 회원님의 ootd를 좋아합니다.");

    private final String baseMessage;

    NotificationType(String baseMessage) {
        this.baseMessage = baseMessage;
    }

    public String makeMessageByUserName(String userName) {
        return userName + this.baseMessage;
    }
}
