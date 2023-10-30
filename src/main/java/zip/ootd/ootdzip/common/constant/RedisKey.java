package zip.ootd.ootdzip.common.constant;

import lombok.Getter;

@Getter
public enum RedisKey {

    VIEWS("views", "viewfilter"),
    UPDATED_VIEWS("updateviews", ""),
    LIKES("likes", ""),
    USER_LIKES("userLikes", "");

    private final String key;

    private final String filterKey;

    RedisKey(String key, String filterKey) {
        this.key = key;
        this.filterKey = filterKey;
    }

    public String makeKeyWith(Long uniqueNumber) {
        return uniqueNumber + "_" + key;
    }

    public String makeFilterKeyWith(Long uniqueNumber) {
        return uniqueNumber + "_" + filterKey;
    }
}
