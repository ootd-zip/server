package zip.ootd.ootdzip.common.constant;

import lombok.Getter;

@Getter
public enum RedisKey {

    OOTD("ootd");

    private final String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String makeKeyWith(Long id) {
        return key + "::" + id;
    }

    public static Long getId(String key) {
        return Long.parseLong(key.split("::")[1]);
    }
}
