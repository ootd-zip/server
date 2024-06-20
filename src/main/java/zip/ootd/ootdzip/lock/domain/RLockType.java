package zip.ootd.ootdzip.lock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RLockType {

    OOTD_VIEW_COUNT("ootdViewCount" + ":RLock:%s", 2L, 5L),
    OOTD_LIKE_COUNT("ootdLikeCount" + ":RLock:%s", 2L, 5L),
    OOTD_BOOKMARK_COUNT("ootdBookmarkCount" + ":RLock:%s", 2L, 5L),
    REPORT_COUNT("reportCount" + ":RLock:%s", 2L, 5L),

    private final String format;
    private final Long waitSecond; // lock 을 얻기 위해 기다리는 시간
    private final Long leaseSeconds; // lock 을 보관하고 있는 시간

    public String getLockKey(String key) {
        return String.format(getFormat(), key);
    }
    }
