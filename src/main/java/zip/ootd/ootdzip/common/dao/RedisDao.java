package zip.ootd.ootdzip.common.dao;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * 구현된 RedisDao DataType
 * String, List, Set
 */
@Component
@RequiredArgsConstructor
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;

    public void setValues(String key, String data) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data);
    }

    public void setValuesList(String key, String data) {
        redisTemplate.opsForList().rightPushAll(key, data);
    }

    public List<String> getValuesList(String key) {
        Long len = redisTemplate.opsForList().size(key);
        if (len == null || len == 0) {
            return new ArrayList<>();
        }
        return redisTemplate.opsForList().range(key, 0, len - 1);
    }

    public void setValues(String key, String data, Duration duration) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        values.set(key, data, duration);
    }

    public String getValues(String key) {
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        return values.get(key);
    }

    public void deleteValues(String key) {
        redisTemplate.delete(key);
    }

    public void setValuesSet(String key, String data) {
        redisTemplate.opsForSet().add(key, data);
    }

    public Set<String> getValuesSet(String key) {
        SetOperations<String, String> values = redisTemplate.opsForSet();
        Long size = values.size(key);
        if (size == null || size == 0) {
            return new HashSet<>();
        }
        return values.members(key);
    }

    public void deleteValuesSet(String key, String value) {
        SetOperations<String, String> values = redisTemplate.opsForSet();
        values.remove(key, value);
    }
}