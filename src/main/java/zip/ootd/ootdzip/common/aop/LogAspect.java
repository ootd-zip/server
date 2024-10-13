package zip.ootd.ootdzip.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("bean(*Controller)")
    private void allRequest() {
    }

    @Around("allRequest()")
    private Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long beforeRequest = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        Object result = joinPoint.proceed();
        long timeTaken = System.currentTimeMillis() - beforeRequest;
        log.debug("[Request] {} time=[{}ms]", methodName, timeTaken);
        return result;
    }
}