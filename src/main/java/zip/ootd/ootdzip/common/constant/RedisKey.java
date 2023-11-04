package zip.ootd.ootdzip.common.constant;

import lombok.Getter;

@Getter
public enum RedisKey {

    VIEWS("views"),
    VIEW_FILTER("viewfilter"),
    UPDATED_VIEWS("updateviews"),
    LIKES("likes"),
    USER_LIKES("userLikes"),
    UPDATED_LIKES("updatelikes");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String makeKeyWith(Long uniqueNumber) {
        return uniqueNumber + "_" + key;
    }
}
