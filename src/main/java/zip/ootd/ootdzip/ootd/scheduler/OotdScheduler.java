package zip.ootd.ootdzip.ootd.scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.constant.RedisKey;
import zip.ootd.ootdzip.common.dao.RedisDao;
import zip.ootd.ootdzip.ootd.domain.Ootd;
import zip.ootd.ootdzip.ootd.repository.OotdRepository;

@Component
@RequiredArgsConstructor
public class OotdScheduler {

    private static final int UPDATE_VIEW_BATCH_SIZE = 100;
    private static final int UPDATE_LIKE_BATCH_SIZE = 100;
    private final RedisDao redisDao;
    private final OotdRepository ootdRepository;

    /**
     * 애플리케이션 시작 후 30초 후에 첫 실행, 그 후 매 10초마다 주기적으로 실행(바뀔예정)
     * <p>
     * Redis 에서 키 가져오는 방법
     * UPDATED_VIEW 에 조회수가 변경된 키들을 등록해둔것을 가져옵니다.
     * Redis 의 모든키를 조회해서 찾지않고 UPDATED_VIEW 와 같이 별도의 키를 사용하는 이유는
     * 레디스는 싱글쓰레드이기 때문에 모든 키를 가져올시 O(n) 연산을 하기 때문에 해당 시간동안 레디스의 작업이 잠시 중단될 가능성이 있습니다.
     * 그래서 UPDATED_VIEW 키를 만들어 변경이 필요한 키를 저장해두고 O(1) 연산으로 가져오게 했습니다.
     * <p>
     * 벌크연산을 안하는 이유
     * id 별로 각각 다른 조회수를 업데이트를 해야되기 때문에 "벌크연산" 이 수행되지 않습니다.
     * 그래서 id 별로 update 쿼리가 발생합니다.
     * <p>
     * 하이버네이트 배치 이용
     * 단순히 update 문을 날리게 되면 update 쿼리마다 db 연결과정이 필요하기때문에
     * 해당 update 문을 한번에 보내기위해 `batch_size` 를 이용했습니다.
     * batch_size 에 설정된 크기만큼 update 문을 한번에 보낼 수 있게하는 하이버네이트 배치를 이용합니다.
     * 하이버네이트 배치를 사용하기 위해서는 변경이 필요한 엔티티에 대한 조회가 필요합니다.
     * <p>
     * 페이지네이션 이용
     * 해당 로직은 조회과정이 있습니다. 많은 량의 데이터를 한번에 조회시 문제가 될 수 있으므로
     * Slice 를 활용해 특정 사이즈만큼 나누어서 조회하도록 했습니다.
     * 사이즈의 경우 slice 조회시 하이버네이트 배치 쿼리가 나가므로, 하이버네이트 배치 사이즈와 동일하게 가져가는걸 추천드립니다.
     * <p>
     * 싱글 쓰레드 스케줄링
     * 현재 쓰레드풀을 따로 구성하지 않았기때문에 싱글쓰레드 스케줄링이 작동합니다.
     * 예를들어 updateViewCount 작동이 끝나야 updateLikeCount 가 작동합니다.
     * 현재는 멀티쓰레드가 필요하지 않다고 판단되어 사용하지 않았지만 추후 스케줄링 작업이 늘어나면 고려할 필요가 있습니다.
     */
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 한번 실행
    public void updateViewCount() {
        String updateViewKey = RedisKey.UPDATED_VIEWS.getKey();
        List<Long> ootdIds = getOotdIds(updateViewKey);

        Slice<Ootd> sliceOotds;
        int pageNumber = 0;

        do {
            sliceOotds = ootdRepository.findAllByIds(ootdIds, PageRequest.of(pageNumber, UPDATE_VIEW_BATCH_SIZE));
            sliceOotds.get().forEach(this::updateViewCountRedisToDb);
            pageNumber++;
        } while (sliceOotds.hasNext());
        redisDao.deleteValues(updateViewKey);
    }

    private void updateViewCountRedisToDb(Ootd ootd) {
        String key = RedisKey.VIEWS.makeKeyWith(ootd.getId());
        int viewCount = Integer.parseInt(redisDao.getValues(key)); // redis 에 저장된 ootd 의 조회수를 가져옴

        ootd.updateViewCount(viewCount);
        redisDao.deleteValues(key);

        String filterKey = RedisKey.VIEW_FILTER.makeKeyWith(ootd.getId());
        redisDao.deleteValues(filterKey);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 한번 실행
    public void updateLikeCount() {
        String updateViewKey = RedisKey.UPDATED_LIKES.getKey();
        List<Long> ootdIds = getOotdIds(updateViewKey);

        Slice<Ootd> sliceOotds;
        int pageNumber = 0;

        do {
            sliceOotds = ootdRepository.findAllByIds(ootdIds, PageRequest.of(pageNumber, UPDATE_LIKE_BATCH_SIZE));
            sliceOotds.get().forEach(this::updateLikeCountRedisToDb);
            pageNumber++;
        } while (sliceOotds.hasNext());

        redisDao.deleteValues(updateViewKey);
    }

    private void updateLikeCountRedisToDb(Ootd ootd) {
        String key = RedisKey.LIKES.makeKeyWith(ootd.getId());
        int likeCount = Integer.parseInt(redisDao.getValues(key)); // redis 에 저장된 ootd 의 조회수를 가져옴

        ootd.updateLikeCount(likeCount);
        redisDao.deleteValues(key);

        String userLike = RedisKey.USER_LIKES.makeKeyWith(ootd.getId());
        redisDao.deleteValues(userLike);
    }

    private List<Long> getOotdIds(String updateViewKey) {
        Set<String> keys = redisDao.getValuesSet(updateViewKey);
        List<Long> ootdIds = new ArrayList<>();

        for (String key : keys) {
            Long id = Long.parseLong(key.split("_")[0]); // ootd 의 id 만 가져옴
            ootdIds.add(id);
        }
        return ootdIds;
    }
}
