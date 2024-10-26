package zip.ootd.ootdzip.lock.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.lock.domain.RLockType;

@Component
@RequiredArgsConstructor
public class RLockService {

    private final RedissonClient redissonClient;

    /**
     * 트랜잭션 전파속성이 NEVER 인 이유는
     * 트랜잭션이 모두 Lock 내부에서 실행되도록 보장하기 위함(반드시 lock 내부에서 트랜잭션이 실행, 커밋된다)
     * 의도치않게 트랜잭션이 시작된 메소드에서 해당 lock 함수를 호출하면, 트랜잭션 내부에 lock 이 들어감으로 이를 막기위함.
     */
    @Transactional(propagation = Propagation.NEVER)
    public Object lock(Supplier<Object> operation, RLockType lockType, String key) {

        String lockKey = lockType.getLockKey(key);
        RLock rLock = redissonClient.getLock(lockKey);

        // 락 획득
        try {
            if (!rLock.tryLock(lockType.getWaitSecond(), lockType.getLeaseSeconds(), TimeUnit.SECONDS)) {
                throw new CustomException(ErrorCode.RLOCK_TIME_OVER);
            }
        } catch (InterruptedException e) {
            throw new CustomException(ErrorCode.RLOCK_GET_FAIL);
        }

        try {
            return operation.get();
        } finally {
            if (rLock.isLocked()) {
                rLock.unlock();
            }
        }
    }
}
