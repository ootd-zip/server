package zip.ootd.ootdzip.lock.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import zip.ootd.ootdzip.common.exception.CustomException;
import zip.ootd.ootdzip.common.exception.code.ErrorCode;
import zip.ootd.ootdzip.lock.annotation.RLockCustom;
import zip.ootd.ootdzip.lock.domain.RLockType;
import zip.ootd.ootdzip.lock.service.RLockService;

@Order(Ordered.HIGHEST_PRECEDENCE + 1) // 트랜잭션+lock 동시에 사용되면 적용 우선순위를 트랜잭션보다 높게
@Aspect
@Component
@RequiredArgsConstructor
public class RLockAspect {

    private final RLockService rLockService;

    @Around("@annotation(rLockCustom)")
    public Object aroundRLock(ProceedingJoinPoint joinPoint, RLockCustom rLockCustom) {

        RLockType lockType = rLockCustom.type();
        String key = rLockCustom.key();

        return rLockService.lock(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new CustomException(ErrorCode.RLOCK_GET_FAIL);
            }
        }, lockType, key);
    }
}
