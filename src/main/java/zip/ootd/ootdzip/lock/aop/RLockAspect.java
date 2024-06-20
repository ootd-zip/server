package zip.ootd.ootdzip.lock.aop;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
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
    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(rLockCustom)")
    public Object aroundRLock(ProceedingJoinPoint joinPoint, RLockCustom rLockCustom) {

        RLockType lockType = rLockCustom.type();
        String key = "";

        StandardEvaluationContext context = createEvaluationContext(joinPoint);

        if (!rLockCustom.key().isEmpty()) {
            key = parser.parseExpression(rLockCustom.key()).getValue(context, String.class);
        }

        if (rLockCustom.keys().length > 0) {
            key = createDynamicKey(joinPoint, rLockCustom.keys(), context);
        }

        return rLockService.lock(() -> {
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new CustomException(ErrorCode.RLOCK_GET_FAIL);
            }
        }, lockType, key);
    }

    private StandardEvaluationContext createEvaluationContext(ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] methodArgs = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        String[] paramNames = methodSignature.getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], methodArgs[i]);
        }
        return context;
    }

    private String createDynamicKey(ProceedingJoinPoint joinPoint, String[] keys, StandardEvaluationContext context) {
        return Arrays.stream(keys)
                .map(key -> parser.parseExpression(key).getValue(context, String.class))
                .collect(Collectors.joining(":"));
    }
}
