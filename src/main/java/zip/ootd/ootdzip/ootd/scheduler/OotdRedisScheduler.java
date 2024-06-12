package zip.ootd.ootdzip.ootd.scheduler;

import java.util.Set;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.constant.RedisKey;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;

/**
 * 해당 스케줄러는 Redis 에 저장된 정보를 DB 에 저장하는 스케줄러입니다.
 */
@Component
@RequiredArgsConstructor
public class OotdRedisScheduler {

    private final RedisDao redisDao;
    private final OotdRepository ootdRepository;

    /**
     * 3분마다 주기적으로 실행
     *
     * 조회수를 주기적으로 Redis -> DB 로 저장하는 작업을 수행합니다.
     */
    @Transactional
    @Scheduled(cron = "0 0/3 * * * ?") // 매일 자정마다 한번 실행
    public void updateViewCountFromRedisToRdb() {

        // OOTD 조회수 키 전체 조회
        Set<String> redisKeys = redisDao.getKeys(RedisKey.OOTDVIEW + "*");

        for (String key : redisKeys) {
            Long ootdId = RedisKey.getId(key);
            Long viewCount = Long.valueOf(redisDao.getValues(key));

            // 조회수 Redis -> DB 반영
            ootdRepository.updateViewCountByOotdId(ootdId, viewCount);

            // OOTD 조회수, OOTD 게시글을 Redis 에서 삭제
            redisDao.deleteValues(key);
            redisDao.deleteValues(RedisKey.OOTD.makeKeyWith(ootdId));
        }
    }
}
