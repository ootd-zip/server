package zip.ootd.ootdzip.common.constant;

public enum RedisKey {

    VIEW("view", "viewfilter"),
    LIKE("like", ""),
    USER_LIKE("userLike", "");

    private final String key;

    private final String filterKey;

    RedisKey(String key, String filterKey) {
        this.key = key;
        this.filterKey = filterKey;
    }

    public String makeKeyWith(Long uniqueNumber) {
        return uniqueNumber + key;
    }

    public String makeFilterKeyWith(Long uniqueNumber) {
        return uniqueNumber + key + filterKey;
    }
}
